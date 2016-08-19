package org.colorcoding.ibas.bobas.test.bo;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.bo.BusinessObjects;

/**
 * 销售订单-行 集合
 */
@XmlType(name = "SalesOrderItems")
@XmlSeeAlso({ SalesOrderItem.class })
public class SalesOrderItems extends BusinessObjects<ISalesOrderItem, ISalesOrder> implements ISalesOrderItems {

	/**
	 * 序列化版本标记
	 */
	private static final long serialVersionUID = 5296887996825885427L;

	/**
	 * 关闭此项，则不再处理集合主键，可提升性能
	 */
	public boolean isSmartPrimaryKeys() {
		return false;
	}

	/**
	 * 构造方法
	 */
	public SalesOrderItems() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param parent
	 *            父项对象
	 */
	public SalesOrderItems(ISalesOrder parent) {
		super(parent);
	}

	/**
	 * 元素类型
	 */
	public Class<?> getElementType() {
		return SalesOrderItem.class;
	}

	/**
	 * 创建销售订单-行
	 * 
	 * @return 销售订单-行
	 */
	public ISalesOrderItem create() {
		ISalesOrderItem item = new SalesOrderItem();
		this.add(item);
		return item;
	}

}
