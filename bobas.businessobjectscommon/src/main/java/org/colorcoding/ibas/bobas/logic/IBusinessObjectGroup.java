package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;

/**
 * 业务对象组接口
 *
 * 表示包含多个子业务对象的组合，逻辑链保存时会遍历子项
 *
 * @author Niuren.Zhu
 *
 */
public interface IBusinessObjectGroup extends IBusinessObject, Iterable<IBusinessObject> {

}