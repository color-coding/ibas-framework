package org.colorcoding.ibas.bobas.expressions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 业务对象的判断链
 * 
 * @author Niuren.Zhu
 *
 */
public class BOJudgmentLinks extends JudgmentLinks {
	/**
	 * 创建属性操作者
	 * 
	 * @return
	 */
	public IPropertyValueOperator createPropertyValueOperator() {
		return new IPropertyValueOperator() {
			private IManageFields value;
			private IFieldData field = null;

			private IFieldData getField() {
				if (this.field == null) {
					this.field = this.value.getField(this.getPropertyName());
				}
				if (this.field == null) {
					throw new JudgmentLinksException(i18n.prop("msg_bobas_not_found_bo_field", this.getPropertyName()));
				}
				return this.field;
			}

			@Override
			public void setValue(Object value) {
				if (value != null && !(value instanceof IManageFields)) {
					throw new JudgmentLinksException(i18n.prop("msg_bobas_invaild_bo_type"));
				}
				this.value = (IManageFields) value;
				this.field = null;
			}

			@Override
			public Object getValue() {
				if (this.value == null) {
					return null;
				}
				return this.getField().getValue();
			}

			@Override
			public Class<?> getValueClass() {
				if (this.value == null) {
					return null;
				}
				return this.getField().getValueType();
			}

			private String propertyName;

			@Override
			public void setPropertyName(String value) {
				this.propertyName = value;
			}

			@Override
			public String getPropertyName() {
				return this.propertyName;
			}

			@Override
			public String toString() {
				return String.format("{property's value: %s}", this.getPropertyName());
			}
		};
	}

	/**
	 * 判断
	 * 
	 * @param bo
	 *            待判断的对象
	 * @return true，满足条件；false，不满足
	 * @throws JudmentOperationException
	 */
	public boolean judge(IBusinessObjectBase bo) throws JudmentOperationException {
		// 无条件
		if (this.getJudgmentItems() == null) {
			return true;
		}
		ArrayList<JudgmentLinkItem> jItems = new ArrayList<>();
		// 设置所以条件的比较值
		for (JudgmentLinkItem item : this.getJudgmentItems()) {
			// 左值
			if (item.getLeftOperter() instanceof IPropertyValueOperator) {
				IPropertyValueOperator propertyOperator = (IPropertyValueOperator) item.getLeftOperter();
				propertyOperator.setValue(bo);
				if (propertyOperator.getPropertyName() != null && !propertyOperator.getPropertyName().isEmpty()
						&& propertyOperator.getPropertyName().indexOf(".") > 0) {
					// 存在子属性的判断
					RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_JUDGMENT_ENTRY_SUB_JUDGMENT, item.toString());
					try {
						// 比较子判断
						boolean result = this.judge(bo, item);
						// 子判断结果并入父项
						JudgmentLinkItem jItem = new JudgmentLinkItem();
						jItem.setRelationship(item.getRelationship());
						jItem.setOpenBracket(item.getOpenBracket());
						jItem.setLeftOperter(this.createValueOperator());
						jItem.getLeftOperter().setValue(true);
						jItem.setOperation(JudmentOperations.EQUAL);
						jItem.setRightOperter(this.createValueOperator());
						jItem.getRightOperter().setValue(result);
						jItem.setCloseBracket(item.getCloseBracket());
						jItems.add(jItem);
						// 处理下一个
						continue;
					} catch (JudmentOperationException e) {
						throw e;
					} catch (Exception e) {
						throw new JudmentOperationException(e);
					}
				}
			}
			// 右值
			if (item.getRightOperter() instanceof IPropertyValueOperator) {
				IPropertyValueOperator propertyOperator = (IPropertyValueOperator) item.getRightOperter();
				propertyOperator.setValue(bo);
			}
			jItems.add(item);
		}
		return this.judge(0, jItems.toArray(new JudgmentLinkItem[] {}));
	}

	private List<IBusinessObjectBase> getValues(IBusinessObjectBase bo, String path) {
		return this.getValues(bo, path.split("\\."));
	}

	private List<IBusinessObjectBase> getValues(IBusinessObjectBase bo, String[] propertys) {
		ArrayList<IBusinessObjectBase> values = new ArrayList<>();
		if (!(bo instanceof IManageFields)) {
			// 不能识别的对象
			return values;
		}
		if (propertys.length == 1) {
			// 最后一个属性
			String property = propertys[0];
			IFieldData fieldData = ((IManageFields) bo).getField(property);
			if (fieldData == null) {
				// 未找到属性
				RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_JUDGMENT_NOT_FOUND_PROPERTY, bo, property);
			} else {
				values.add(bo);
			}
			return values;
		}
		// 获取属性路径的值
		String property = null;
		for (int i = 0; i < propertys.length - 1; i++) {
			property = propertys[i];
			IFieldData fieldData = ((IManageFields) bo).getField(property);
			if (fieldData == null) {
				// 未找到属性
				RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_JUDGMENT_NOT_FOUND_PROPERTY, bo, property);
				break;
			}
			String[] lasts = new String[propertys.length - i - 1];
			for (int j = 0; j < lasts.length; j++) {
				lasts[j] = propertys[i + j + 1];
			}
			if (fieldData.getValue() instanceof IBusinessObjectListBase<?>) {
				// 值是业务对象列表
				IBusinessObjectListBase<?> boList = (IBusinessObjectListBase<?>) fieldData.getValue();
				for (IBusinessObjectBase item : boList) {
					values.addAll(this.getValues(item, lasts));
				}
			} else if (fieldData.getValue() instanceof IBusinessObjectBase) {
				// 值是对象
				values.add((IBusinessObjectBase) fieldData.getValue());
			}
		}
		return values;
	}

	protected boolean judge(IBusinessObjectBase bo, JudgmentLinkItem parent)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, JudmentOperationException {
		String path = ((IPropertyValueOperator) parent.getLeftOperter()).getPropertyName();
		if (path == null || path.isEmpty()) {
			// 无属性
			return false;
		}
		List<IBusinessObjectBase> values = this.getValues(bo, path);
		if (values.isEmpty()) {
			// 没有待比较的值
			return false;
		}
		String property = path.substring(path.lastIndexOf(".") + 1);
		ArrayList<JudgmentLinkItem> jItems = new ArrayList<>();
		for (Object item : values) {
			JudgmentLinkItem jItem = new JudgmentLinkItem();
			jItem.setRelationship(JudmentOperations.OR);
			// 左值
			IPropertyValueOperator propertyValueOperator = this.createPropertyValueOperator();
			propertyValueOperator.setPropertyName(property);
			propertyValueOperator.setValue(item);
			jItem.setLeftOperter(propertyValueOperator);
			// 比较类型
			jItem.setOperation(parent.getOperation());
			// 右值
			jItem.setRightOperter(parent.getRightOperter());
			if (jItem.getRightOperter() instanceof IPropertyValueOperator) {
				// 属性值操作
				jItem.getRightOperter().setValue(item);
			}
			jItems.add(jItem);
		}
		return this.judge(0, jItems.toArray(new JudgmentLinkItem[] {}));
	}

}
