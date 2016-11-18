package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 被引用的业务对象
 * 
 * 继承此接口，对象执行删除操作时，如果已被引用，则仅标记对象为删除；反之则删除。
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOReferenced {
	/**
	 * 是否被引用
	 * 
	 * @return
	 */
	emYesNo getReferenced();

	/**
	 * 设置-引用状态
	 * 
	 * @param value
	 */
	void setReferenced(emYesNo value);
}
