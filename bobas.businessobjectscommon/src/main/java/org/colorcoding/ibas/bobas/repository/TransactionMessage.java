package org.colorcoding.ibas.bobas.repository;

import javax.xml.bind.annotation.XmlElement;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 事务消息
 * 
 * @author Niuren.Zhu
 *
 */
public class TransactionMessage extends BusinessObject<TransactionMessage> implements IBusinessObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2602868482377739486L;

	/**
	 * 当前类型
	 */
	private final static Class<?> MY_CLASS = TransactionMessage.class;

	/**
	 * 编码 属性
	 */
	@DbField(name = "Code", type = DbFieldType.db_Numeric, savable = false)
	public final static IPropertyInfo<Integer> CodeProperty = registerProperty("Code", Integer.class, MY_CLASS);

	/**
	 * 获取-编码
	 * 
	 * @return 值
	 */
	@XmlElement(name = "Code")
	public final Integer getCode() {
		return this.getProperty(CodeProperty);
	}

	/**
	 * 设置-编码
	 * 
	 * @param value
	 *            值
	 */
	public final void setCode(Integer value) {
		this.setProperty(CodeProperty, value);
	}

	/**
	 * 消息 属性
	 */
	@DbField(name = "Message", type = DbFieldType.db_Alphanumeric, savable = false)
	public final static IPropertyInfo<String> MessageProperty = registerProperty("Message", String.class, MY_CLASS);

	/**
	 * 获取-消息
	 * 
	 * @return 值
	 */
	@XmlElement(name = "Message")
	public final String getMessage() {
		return this.getProperty(MessageProperty);
	}

	/**
	 * 设置-消息
	 * 
	 * @param value
	 *            值
	 */
	public final void setMessage(String value) {
		this.setProperty(MessageProperty, value);
	}

}
