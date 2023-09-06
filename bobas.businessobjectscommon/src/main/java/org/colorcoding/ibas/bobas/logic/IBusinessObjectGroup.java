package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;

/**
 * 业务对象组
 * 
 * 保存时遍历子项内容
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessObjectGroup extends IBusinessObject, Iterable<IBusinessObject> {

}
