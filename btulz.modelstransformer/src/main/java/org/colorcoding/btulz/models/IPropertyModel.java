package org.colorcoding.btulz.models;

/**
 * 模型类型属性
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPropertyModel extends IProperty {

	/**
	 * 获取-模型
	 * 
	 * @return
	 */
	IModel getModel();

	/**
	 * 设置-模型
	 * 
	 * @param model
	 */
	void setModel(IModel model);
}
