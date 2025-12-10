package org.colorcoding.ibas.bobas.logic.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBOCustomKey;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;
import org.colorcoding.ibas.bobas.db.IDbTableUpdate;

/**
 * 业务对象日志
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = BOLogst.BUSINESS_OBJECT_NAME, namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
@XmlRootElement(name = BOLogst.BUSINESS_OBJECT_NAME, namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public class BOLogst extends BusinessObject<BOLogst> implements IBOCustomKey, IDbTableUpdate {

	/**
	 * 序列化版本标记
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 当前类型
	 */
	private static final Class<?> MY_CLASS = BOLogst.class;

	/**
	 * 数据库表
	 */
	public static final String DB_TABLE_NAME = "${Company}_SYS_BOLOGST";

	/**
	 * 业务对象名称
	 */
	public static final String BUSINESS_OBJECT_NAME = "BOLogst";

	/**
	 * 属性名称-类型
	 */
	private static final String PROPERTY_BOCODE_NAME = "BOCode";

	/**
	 * 类型 属性
	 */
	@DbField(name = "BOCode", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME, primaryKey = true)
	public static final IPropertyInfo<String> PROPERTY_BOCODE = registerProperty(PROPERTY_BOCODE_NAME, String.class,
			MY_CLASS);

	/**
	 * 获取-类型
	 * 
	 * @return 值
	 */
	public final String getBOCode() {
		return this.getProperty(PROPERTY_BOCODE);
	}

	/**
	 * 设置-类型
	 * 
	 * @param value 值
	 */
	public final void setBOCode(String value) {
		this.setProperty(PROPERTY_BOCODE, value);
	}

	/**
	 * 属性名称-主键值
	 */
	private static final String PROPERTY_BOKEYS_NAME = "BOKeys";

	/**
	 * 主键值 属性
	 */
	@DbField(name = "BOKeys", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME, primaryKey = true)
	public static final IPropertyInfo<String> PROPERTY_BOKEYS = registerProperty(PROPERTY_BOKEYS_NAME, String.class,
			MY_CLASS);

	/**
	 * 获取-主键值
	 * 
	 * @return 值
	 */
	public final String getBOKeys() {
		return this.getProperty(PROPERTY_BOKEYS);
	}

	/**
	 * 设置-主键值
	 * 
	 * @param value 值
	 */
	public final void setBOKeys(String value) {
		this.setProperty(PROPERTY_BOKEYS, value);
	}

	/**
	 * 属性名称-实例号
	 */
	private static final String PROPERTY_LOGINST_NAME = "LogInst";

	/**
	 * 实例号 属性
	 */
	@DbField(name = "LogInst", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME, primaryKey = true)
	public static final IPropertyInfo<Integer> PROPERTY_LOGINST = registerProperty(PROPERTY_LOGINST_NAME, Integer.class,
			MY_CLASS);

	/**
	 * 获取-实例号
	 * 
	 * @return 值
	 */
	public final Integer getLogInst() {
		return this.getProperty(PROPERTY_LOGINST);
	}

	/**
	 * 设置-实例号
	 * 
	 * @param value 值
	 */
	public final void setLogInst(Integer value) {
		this.setProperty(PROPERTY_LOGINST, value);
	}

	/**
	 * 属性名称-修改用户
	 */
	private static final String PROPERTY_MODIFYUSER_NAME = "ModifyUser";

	/**
	 * 修改用户 属性
	 */
	@DbField(name = "Modifier", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<Integer> PROPERTY_MODIFYUSER = registerProperty(PROPERTY_MODIFYUSER_NAME,
			Integer.class, MY_CLASS);

	/**
	 * 获取-修改用户
	 * 
	 * @return 值
	 */
	public final Integer getModifyUser() {
		return this.getProperty(PROPERTY_MODIFYUSER);
	}

	/**
	 * 设置-修改用户
	 * 
	 * @param value 值
	 */
	public final void setModifyUser(Integer value) {
		this.setProperty(PROPERTY_MODIFYUSER, value);
	}

	/**
	 * 属性名称-修改日期
	 */
	private static final String PROPERTY_MODIFYDATE_NAME = "ModifyDate";

	/**
	 * 修改日期 属性
	 */
	@DbField(name = "ModifyDate", type = DbFieldType.DATE, table = DB_TABLE_NAME)
	public static final IPropertyInfo<DateTime> PROPERTY_MODIFYDATE = registerProperty(PROPERTY_MODIFYDATE_NAME,
			DateTime.class, MY_CLASS);

	/**
	 * 获取-修改日期
	 * 
	 * @return 值
	 */
	public final DateTime getModifyDate() {
		return this.getProperty(PROPERTY_MODIFYDATE);
	}

	/**
	 * 设置-修改日期
	 * 
	 * @param value 值
	 */
	public final void setModifyDate(DateTime value) {
		this.setProperty(PROPERTY_MODIFYDATE, value);
	}

	/**
	 * 属性名称-修改时间
	 */
	private static final String PROPERTY_MODIFYTIME_NAME = "ModifyTime";

	/**
	 * 修改时间 属性
	 */
	@DbField(name = "ModifyTime", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<Short> PROPERTY_MODIFYTIME = registerProperty(PROPERTY_MODIFYTIME_NAME,
			Short.class, MY_CLASS);

	/**
	 * 获取-修改时间
	 * 
	 * @return 值
	 */
	public final Short getModifyTime() {
		return this.getProperty(PROPERTY_MODIFYTIME);
	}

	/**
	 * 设置-修改时间
	 * 
	 * @param value 值
	 */
	public final void setModifyTime(Short value) {
		this.setProperty(PROPERTY_MODIFYTIME, value);
	}

	/**
	 * 属性名称-事务标识
	 */
	private static final String PROPERTY_TRANSATIONID_NAME = "TransationId";

	/**
	 * 事务标识 属性
	 */
	@DbField(name = "TransationId", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<String> PROPERTY_TRANSATIONID = registerProperty(PROPERTY_TRANSATIONID_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-事务标识
	 * 
	 * @return 值
	 */
	public final String getTransationId() {
		return this.getProperty(PROPERTY_TRANSATIONID);
	}

	/**
	 * 设置-事务标识
	 * 
	 * @param value 值
	 */
	public final void setTransationId(String value) {
		this.setProperty(PROPERTY_TRANSATIONID, value);
	}

	/**
	 * 属性名称-动机
	 */
	private static final String PROPERTY_CAUSE_NAME = "Cause";

	/**
	 * 动机 属性
	 */
	@DbField(name = "Cause", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<String> PROPERTY_CAUSE = registerProperty(PROPERTY_CAUSE_NAME, String.class,
			MY_CLASS);

	/**
	 * 获取-动机
	 * 
	 * @return 值
	 */
	public final String getCause() {
		return this.getProperty(PROPERTY_CAUSE);
	}

	/**
	 * 设置-动机
	 * 
	 * @param value 值
	 */
	public final void setCause(String value) {
		this.setProperty(PROPERTY_CAUSE, value);
	}

	/**
	 * 属性名称-内容
	 */
	private static final String PROPERTY_CONTENT_NAME = "Content";

	/**
	 * 内容 属性
	 */
	@DbField(name = "Content", type = DbFieldType.MEMO, table = DB_TABLE_NAME)
	public static final IPropertyInfo<String> PROPERTY_CONTENT = registerProperty(PROPERTY_CONTENT_NAME, String.class,
			MY_CLASS);

	/**
	 * 获取-内容
	 * 
	 * @return 值
	 */
	public final String getContent() {
		return this.getProperty(PROPERTY_CONTENT);
	}

	/**
	 * 设置-内容
	 * 
	 * @param value 值
	 */
	public final void setContent(String value) {
		this.setProperty(PROPERTY_CONTENT, value);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();// 基类初始化，不可去除

	}

}
