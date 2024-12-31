package org.colorcoding.ibas.bobas.test.demo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;

@BusinessObjectUnit(code = "CC_SL_SALESORDER")
@XmlRootElement(name = "SalesOrder", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public class SalesOrder extends Order implements IBOUserFields {

	private static final long serialVersionUID = 1656677742867240562L;

	private final static Class<?> MY_CLASS = SalesOrder.class;

	/**
	 * 属性名称-客户代码
	 */
	private static final String PROPERTY_CUSTOMERCODE_NAME = "CustomerCode";

	/**
	 * 客户代码 属性
	 */
	@DbField(name = "CardCode", type = DbFieldType.ALPHANUMERIC, table = Order.DB_TABLE_NAME)
	public static final IPropertyInfo<String> PROPERTY_CUSTOMERCODE = registerProperty(PROPERTY_CUSTOMERCODE_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-客户代码
	 * 
	 * @return 值
	 */
	@XmlElement(name = PROPERTY_CUSTOMERCODE_NAME)
	public final String getCustomerCode() {
		return this.getProperty(PROPERTY_CUSTOMERCODE);
	}

	/**
	 * 设置-客户代码
	 * 
	 * @param value 值
	 */
	public final void setCustomerCode(String value) {
		this.setProperty(PROPERTY_CUSTOMERCODE, value);
	}

	/**
	 * 属性名称-客户名称
	 */
	private static final String PROPERTY_CUSTOMERNAME_NAME = "CustomerName";

	/**
	 * 客户名称 属性
	 */
	@DbField(name = "CardName", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<String> PROPERTY_CUSTOMERNAME = registerProperty(PROPERTY_CUSTOMERNAME_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-客户名称
	 * 
	 * @return 值
	 */
	@XmlElement(name = PROPERTY_CUSTOMERNAME_NAME)
	public final String getCustomerName() {
		return this.getProperty(PROPERTY_CUSTOMERNAME);
	}

	/**
	 * 设置-客户名称
	 * 
	 * @param value 值
	 */
	public final void setCustomerName(String value) {
		this.setProperty(PROPERTY_CUSTOMERNAME, value);
	}

	/**
	 * 属性名称-销售订单-行
	 */
	private static final String PROPERTY_SALESORDERITEMS_NAME = "SalesOrderItems";

	/**
	 * 销售订单-行的集合属性
	 * 
	 */
	public static final IPropertyInfo<SalesOrderItems> PROPERTY_SALESORDERITEMS = registerProperty(
			PROPERTY_SALESORDERITEMS_NAME, SalesOrderItems.class, MY_CLASS);

	/**
	 * 获取-销售订单-行集合
	 * 
	 * @return 值
	 */
	@XmlElementWrapper(name = PROPERTY_SALESORDERITEMS_NAME)
	@XmlElement(name = SalesOrderItem.BUSINESS_OBJECT_NAME, type = SalesOrderItem.class)
	public SalesOrderItems getSalesOrderItems() {
		return this.getProperty(PROPERTY_SALESORDERITEMS);
	}

	/**
	 * 设置-销售订单-行集合
	 * 
	 * @param value 值
	 */
	public final void setSalesOrderItems(SalesOrderItems value) {
		this.setProperty(PROPERTY_SALESORDERITEMS, value);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.setSalesOrderItems(new SalesOrderItems(this));
	}
}
