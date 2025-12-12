package org.colorcoding.ibas.bobas.expression;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
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
		return new FieldValueOperator();
	}

	/**
	 * 判断
	 * 
	 * @param bo 待判断的对象
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
				if (!Strings.isNullOrEmpty(propertyOperator.getPropertyName())
						&& propertyOperator.getPropertyName().indexOf(".") > 0
						&& item.getLeftOperter() instanceof FieldValueOperator) {
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
		return this.judge(0, jItems.toArray(new JudgmentLinkItem[jItems.size()]));
	}

	private List<IBusinessObject> getValues(IBusinessObject bo, String path) {
		return this.getValues(bo, path.split("\\."));
	}

	private List<IBusinessObject> getValues(IBusinessObject bo, String[] propertys) {
		ArrayList<IBusinessObject> values = new ArrayList<>();
		if (!BOUtilities.isBusinessObject(bo)) {
			// 不能识别的对象
			return values;
		}
		String property = null;
		if (propertys.length == 1) {
			// 最后一个属性
			property = propertys[0];
			IPropertyInfo<?> propertyInfo = BOUtilities.propertyInfo(bo, property);
			if (propertyInfo == null) {
				// 未找到属性
				Logger.log(MessageLevel.WARN, "not found %s property %s.", bo, property);
			} else {
				values.add(bo);
			}
			return values;
		}
		// 获取属性路径的值
		for (int i = 0; i < propertys.length - 1; i++) {
			property = propertys[i];
			IPropertyInfo<?> propertyInfo = BOUtilities.propertyInfo(bo, property);
			if (propertyInfo == null) {
				// 未找到属性
				Logger.log(MessageLevel.WARN, "not found %s property %s.", bo, property);
				break;
			}
			String[] lasts = new String[propertys.length - i - 1];
			for (int j = 0; j < lasts.length; j++) {
				lasts[j] = propertys[i + j + 1];
			}
			if (BOUtilities.propertyValue(bo, property) instanceof IBusinessObjects<?, ?>) {
				// 值是业务对象列表
				IBusinessObjects<?, ?> boList = BOUtilities.propertyValue(bo, property);
				for (IBusinessObject item : boList) {
					values.addAll(this.getValues(item, lasts));
				}
			} else if (BOUtilities.propertyValue(bo, property) instanceof IBusinessObject) {
				// 值是对象
				values.add(BOUtilities.propertyValue(bo, property));
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
			IValueOperator valueOperator = this.createValueOperator();
			if (parent.getRightOperter() instanceof FieldValueOperator) {
				parent.getRightOperter().setValue(item);
			}
			valueOperator.setValue(parent.getRightOperter().getValue());
			// 设置右值
			jItem.setRightOperter(valueOperator);
			jItems.add(jItem);
		}
		return this.judge(0, jItems.toArray(new JudgmentLinkItem[jItems.size()]));
	}

}
