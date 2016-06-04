package org.colorcoding.ibas.btulz.models;

import java.util.List;

/**
 * 模型集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IModels extends List<IModel> {
	/**
	 * 创建并添加模型
	 * 
	 * @return
	 */
	IModel create();
}
