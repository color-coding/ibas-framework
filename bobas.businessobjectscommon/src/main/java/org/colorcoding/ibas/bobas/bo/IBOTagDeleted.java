package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 标记删除，已被引用的对象标记删除
 *
 * @author Niuren.Zhu
 *
 */
public interface IBOTagDeleted extends IBOTagReferenced {

	/**
	 * 获取删除状态
	 *
	 * @return 是否删除
	 */
	emYesNo getDeleted();

	/**
	 * 设置删除状态
	 *
	 * @param value 删除状态
	 */
	void setDeleted(emYesNo value);
}
