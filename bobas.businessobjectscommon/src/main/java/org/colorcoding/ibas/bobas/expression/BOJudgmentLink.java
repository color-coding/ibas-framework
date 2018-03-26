package org.colorcoding.ibas.bobas.expression;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 业务对象的判断链
 * 
 * @author Niuren.Zhu
 *
 */
public class BOJudgmentLink extends JudgmentLink {
	/**
	 * 创建属性操作者
	 * 
	 * @return
	 */
	protected IPropertyValueOperator createPropertyValueOperator() {
		return new IPropertyValueOperator() {
			private IManageFields value;
			private IFieldData field = null;

			private IFieldData getField() {
				if (this.field == null) {
					this.field = this.value.getField(this.getPropertyName());
				}
				if (this.field == null) {
					throw new JudgmentLinkException(I18N.prop("msg_bobas_not_found_bo_field", this.getPropertyName()));
				}
				return this.field;
			}

			@Override
			public void setValue(Object value) {
				if (value != null && !(value instanceof IManageFields)) {
					throw new JudgmentLinkException(I18N.prop("msg_bobas_invaild_bo_type"));
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
	public boolean judge(IBusinessObject bo) throws JudmentOperationException {
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
					if (MyConfiguration.isDebugMode()) {
						Logger.log(MessageLevel.DEBUG, MSG_JUDGMENT_ENTRY_SUB_JUDGMENT, item.toString());
					}
					try {
						// 比较子判断
						boolean result = this.judge(bo, item);
						// 子判断结果并入父项
						JudgmentLinkItem jItem = new JudgmentLinkItem();
						jItem.setRelationship(item.getRelationship());
						jItem.setOpenBracket(item.getOpenBracket());
						jItem.setLeftOperter(this.createValueOperator());
						jItem.getLeftOperter().setValue(true);
						jItem.setOperation(JudmentOperation.EQUAL);
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

	private List<IBusinessObject> getValues(IBusinessObject bo, String path) {
		return this.getValues(bo, path.split("\\."));
	}

	private List<IBusinessObject> getValues(IBusinessObject bo, String[] propertys) {
		ArrayList<IBusinessObject> values = new ArrayList<>();
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
				Logger.log(MessageLevel.DEBUG, MSG_JUDGMENT_NOT_FOUND_PROPERTY, bo, property);
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
				Logger.log(MessageLevel.DEBUG, MSG_JUDGMENT_NOT_FOUND_PROPERTY, bo, property);
				break;
			}
			String[] lasts = new String[propertys.length - i - 1];
			for (int j = 0; j < lasts.length; j++) {
				lasts[j] = propertys[i + j + 1];
			}
			if (fieldData.getValue() instanceof IBusinessObjects<?, ?>) {
				// 值是业务对象列表
				IBusinessObjects<?, ?> boList = (IBusinessObjects<?, ?>) fieldData.getValue();
				for (IBusinessObject item : boList) {
					values.addAll(this.getValues(item, lasts));
				}
			} else if (fieldData.getValue() instanceof IBusinessObject) {
				// 值是对象
				values.add((IBusinessObject) fieldData.getValue());
			}
		}
		return values;
	}

	protected boolean judge(IBusinessObject bo, JudgmentLinkItem parent)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, JudmentOperationException {
		String path = ((IPropertyValueOperator) parent.getLeftOperter()).getPropertyName();
		if (path == null || path.isEmpty()) {
			// 无属性
			return false;
		}
		List<IBusinessObject> values = this.getValues(bo, path);
		if (values.isEmpty()) {
			// 没有待比较的值
			return false;
		}
		String property = path.substring(path.lastIndexOf(".") + 1);
		ArrayList<JudgmentLinkItem> jItems = new ArrayList<>();
		for (Object item : values) {
			JudgmentLinkItem jItem = new JudgmentLinkItem();
			jItem.setRelationship(JudmentOperation.OR);
			// 左值
			IPropertyValueOperator propertyValueOperator = this.createPropertyValueOperator();
			propertyValueOperator.setPropertyName(property);
			propertyValueOperator.setValue(item);
			jItem.setLeftOperter(propertyValueOperator);
			// 比较类型
			jItem.setOperation(parent.getOperation());
			// 获取右值，使用父项的运算结果避免藏数据
			parent.getRightOperter().setValue(item);
			IValueOperator valueOperator = this.createValueOperator();
			valueOperator.setValue(parent.getRightOperter().getValue());
			// 设置右值
			jItem.setRightOperter(valueOperator);
			jItems.add(jItem);
		}
		return this.judge(0, jItems.toArray(new JudgmentLinkItem[] {}));
	}

}
