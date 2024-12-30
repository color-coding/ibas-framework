package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 属性最大值编号
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOMaxValueKey {
	/**
	 * 增长步长
	 * 
	 * @return
	 */
	default int getMaxValueStep() {
		return 1;
	}

	/**
	 * 获取-最大值属性
	 * 
	 * @return
	 */
	IPropertyInfo<?> getMaxValueField();

	/**
	 * 获取-最大值条件属性
	 * 
	 * @return 条件
	 */
	IPropertyInfo<?>[] getMaxValueConditions();

	/**
	 * 设置最大值
	 * 
	 * @param value
	 * @return
	 */
	boolean setMaxValue(Integer value);
}
