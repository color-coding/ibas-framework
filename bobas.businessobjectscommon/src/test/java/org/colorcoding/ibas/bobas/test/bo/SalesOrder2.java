package org.colorcoding.ibas.bobas.test.bo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public class SalesOrder2 extends SalesOrder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -243448784277376241L;

	/**
	 * 当前类型
	 */
	private final static Class<?> MY_CLASS = SalesOrder2.class;
	/**
	 * 凭证编号 属性
	 */
	@DbField(name = "DocEntry2", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> DocEntry2Property = registerProperty("DocEntry2", Integer.class,
			MY_CLASS);

	/**
	 * 获取-凭证编号2
	 *
	 * @return 值
	 */
	@XmlElement(name = "DocEntry2")
	public final Integer getDocEntry2() {
		return this.getProperty(DocEntry2Property);
	}

	/**
	 * 设置-凭证编号2
	 *
	 * @param value
	 *            值
	 */
	public final void setDocEntry2(Integer value) {
		this.setProperty(DocEntry2Property, value);
	}

	@Override
	@XmlElementWrapper(name = "SalesOrderItems")
	@XmlElement(name = "SalesOrderItem", type = SalesOrderItem2.class, required = true)
	public ISalesOrderItems getSalesOrderItems() {
		return this.getProperty(SalesOrderItemsProperty);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		this.setSalesOrderItems(new SalesOrderItems2(this));
		super.initialize();

	}
}
