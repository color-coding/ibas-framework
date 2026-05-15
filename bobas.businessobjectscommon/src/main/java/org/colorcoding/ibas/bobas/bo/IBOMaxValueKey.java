package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 属性最大值编号（用于自动分配递增主键）
 *
 * @author Niuren.Zhu
 *
 */
public interface IBOMaxValueKey {
	/**
	 * 增长步长
	 *
	 * @return 步长值（默认1）
	 */
	default int getMaxValueStep() {
		return 1;
	}

	/**
	 * 获取-最大值属性
	 *
	 * @return 最大值对应的属性信息
	 */
	IPropertyInfo<?> getMaxValueField();

	/**
	 * 获取-最大值条件属性（用于限定查询范围的属性数组）
	 *
	 * @return 条件属性数组
	 */
	IPropertyInfo<?>[] getMaxValueConditions();

	/**
	 * 设置最大值
	 *
	 * @param value 最大值
	 * @return 是否设置成功
	 */
	boolean setMaxValue(Integer value);
}