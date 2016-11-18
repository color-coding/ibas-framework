package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 标记删除业务对象
 * 
 * 应用在数据在使用后，不能物理上删除，仅做删除标记
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOTagDeleted extends IBOReferenced {

	/**
	 * 是否标记删除
	 * 
	 * @return
	 */
	emYesNo getDeleted();

	/**
	 * 设置-删除状态
	 * 
	 * @param value
	 */
	void setDeleted(emYesNo value);
}
