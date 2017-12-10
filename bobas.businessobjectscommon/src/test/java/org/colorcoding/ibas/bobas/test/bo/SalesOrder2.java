package org.colorcoding.ibas.bobas.test.bo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public class SalesOrder2 extends SalesOrder {

	private static final long serialVersionUID = -243448784277376241L;

	/**
	 * 当前类型
	 */
	private final static Class<?> MY_CLASS = SalesOrder2.class;

	/**
	 * 属性名称-凭证编号
	 */
	private static final String PROPERTY_DOCENTRY2_NAME = "DocEntry2";

	/**
	 * 凭证编号 属性
	 */
	@DbField(name = "DocEntry2", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME, primaryKey = true)
	public static final IPropertyInfo<Integer> PROPERTY_DOCENTRY2 = registerProperty(PROPERTY_DOCENTRY2_NAME,
			Integer.class, MY_CLASS);

	/**
	 * 获取-凭证编号
	 * 
	 * @return 值
	 */
	@XmlElement(name = PROPERTY_DOCENTRY2_NAME)
	public final Integer getDocEntry2() {
		return this.getProperty(PROPERTY_DOCENTRY2);
	}

	/**
	 * 设置-凭证编号
	 * 
	 * @param value
	 *            值
	 */
	public final void setDocEntry2(Integer value) {
		this.setProperty(PROPERTY_DOCENTRY2, value);
	}

	@Override
	@XmlElementWrapper(name = "SalesOrderItems")
	@XmlElement(name = "SalesOrderItem", type = SalesOrderItem2.class, required = true)
	public ISalesOrderItems getSalesOrderItems() {
		return this.getProperty(PROPERTY_SALESORDERITEMS);
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
