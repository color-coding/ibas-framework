package org.colorcoding.ibas.btulz.models;

import java.util.List;

import org.colorcoding.ibas.btulz.data.emPropertyType;

/**
 * 属性集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IProperties extends List<IProperty> {
	/**
	 * 创建并添加属性
	 * 
	 * @return
	 */
	IProperty create(emPropertyType type);
}
