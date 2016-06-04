package org.colorcoding.ibas.btulz.models;

/**
 * 模型类型属性集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPropertyModels extends IProperty {
	/**
	 * 获取-模型集合
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
