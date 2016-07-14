package org.colorcoding.ibas.bobas.test.bo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.mapping.BOCode;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 获取-用户
 * 
 */
@XmlType(name = "User")
@XmlRootElement(name = "User")
@BOCode(User.BUSINESS_OBJECT_CODE)
public class User extends BusinessObject<User> implements IUser {

	/**
	 * 序列化版本标记
	 */
	private static final long serialVersionUID = 4890094219444963642L;

	/**
	 * 当前类型
	 */
	private final static Class<?> MY_CLASS = User.class;

	/**
	 * 数据库表
	 */
	public final static String DB_TABLE_NAME = "CC_TT_USER";

	/**
	 * 业务对象编码
	 */
	public final static String BUSINESS_OBJECT_CODE = "CC_TT_USER";

	/**
	 * 编码 属性
	 */
	@DbField(name = "UserCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = true)
	public final static IPropertyInfo<String> UserCodeProperty = registerProperty("UserCode", String.class, MY_CLASS);

	/**
	 * 获取-编码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UserCode")
	public final String getUserCode() {
		return this.getProperty(UserCodeProperty);
	}

	/**
	 * 设置-编码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUserCode(String value) {
		this.setProperty(UserCodeProperty, value);
	}

	/**
	 * 名称 属性
	 */
	@DbField(name = "UserName", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> UserNameProperty = registerProperty("UserName", String.class, MY_CLASS);

	/**
	 * 获取-名称
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UserName")
	public final String getUserName() {
		return this.getProperty(UserNameProperty);
	}

	/**
	 * 设置-名称
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUserName(String value) {
		this.setProperty(UserNameProperty, value);
	}

	/**
	 * 用户密码 属性
	 */
	@DbField(name = "PassWord", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> UserPasswordProperty = registerProperty("UserPassword", String.class,
			MY_CLASS);

	/**
	 * 获取-用户密码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UserPassword")
	public final String getUserPassword() {
		return this.getProperty(UserPasswordProperty);
	}

	/**
	 * 设置-用户密码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUserPassword(String value) {
		this.setProperty(UserPasswordProperty, value);
	}

	/**
	 * 激活 属性
	 */
	@DbField(name = "Activated", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> ActivatedProperty = registerProperty("Activated", emYesNo.class,
			MY_CLASS);

	/**
	 * 获取-激活
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Activated")
	public final emYesNo getActivated() {
		return this.getProperty(ActivatedProperty);
	}

	/**
	 * 设置-激活
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setActivated(emYesNo value) {
		this.setProperty(ActivatedProperty, value);
	}

	/**
	 * 超级用户 属性
	 */
	@DbField(name = "SupperUser", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> SupperUserProperty = registerProperty("SupperUser", emYesNo.class,
			MY_CLASS);

	/**
	 * 获取-超级用户
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "SupperUser")
	public final emYesNo getSupperUser() {
		return this.getProperty(SupperUserProperty);
	}

	/**
	 * 设置-超级用户
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setSupperUser(emYesNo value) {
		this.setProperty(SupperUserProperty, value);
	}

	/**
	 * 对象类型 属性
	 */
	@DbField(name = "ObjectCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> ObjectCodeProperty = registerProperty("ObjectCode", String.class,
			MY_CLASS);

	/**
	 * 获取-对象类型
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ObjectCode")
	public final String getObjectCode() {
		return this.getProperty(ObjectCodeProperty);
	}

	/**
	 * 设置-对象类型
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setObjectCode(String value) {
		this.setProperty(ObjectCodeProperty, value);
	}

	/**
	 * 数据源 属性
	 */
	@DbField(name = "DataSource", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> DataSourceProperty = registerProperty("DataSource", String.class,
			MY_CLASS);

	/**
	 * 获取-数据源
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DataSource")
	public final String getDataSource() {
		return this.getProperty(DataSourceProperty);
	}

	/**
	 * 设置-数据源
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDataSource(String value) {
		this.setProperty(DataSourceProperty, value);
	}

	/**
	 * 对象编号 属性
	 */
	@DbField(name = "ObjectKey", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = true)
	public final static IPropertyInfo<Integer> ObjectKeyProperty = registerProperty("ObjectKey", Integer.class,
			MY_CLASS);

	/**
	 * 获取-对象编号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ObjectKey")
	public final Integer getObjectKey() {
		return this.getProperty(ObjectKeyProperty);
	}

	/**
	 * 设置-对象编号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setObjectKey(Integer value) {
		this.setProperty(ObjectKeyProperty, value);
	}

	/**
	 * 创建日期 属性
	 */
	@DbField(name = "CreateDate", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<DateTime> CreateDateProperty = registerProperty("CreateDate", DateTime.class,
			MY_CLASS);

	/**
	 * 获取-创建日期
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "CreateDate")
	public final DateTime getCreateDate() {
		return this.getProperty(CreateDateProperty);
	}

	/**
	 * 设置-创建日期
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCreateDate(DateTime value) {
		this.setProperty(CreateDateProperty, value);
	}

	/**
	 * 创建时间 属性
	 */
	@DbField(name = "CreateTime", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Short> CreateTimeProperty = registerProperty("CreateTime", Short.class, MY_CLASS);

	/**
	 * 获取-创建时间
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "CreateTime")
	public final Short getCreateTime() {
		return this.getProperty(CreateTimeProperty);
	}

	/**
	 * 设置-创建时间
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCreateTime(Short value) {
		this.setProperty(CreateTimeProperty, value);
	}

	/**
	 * 修改日期 属性
	 */
	@DbField(name = "UpdateDate", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<DateTime> UpdateDateProperty = registerProperty("UpdateDate", DateTime.class,
			MY_CLASS);

	/**
	 * 获取-修改日期
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UpdateDate")
	public final DateTime getUpdateDate() {
		return this.getProperty(UpdateDateProperty);
	}

	/**
	 * 设置-修改日期
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUpdateDate(DateTime value) {
		this.setProperty(UpdateDateProperty, value);
	}

	/**
	 * 修改时间 属性
	 */
	@DbField(name = "UpdateTime", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Short> UpdateTimeProperty = registerProperty("UpdateTime", Short.class, MY_CLASS);

	/**
	 * 获取-修改时间
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UpdateTime")
	public final Short getUpdateTime() {
		return this.getProperty(UpdateTimeProperty);
	}

	/**
	 * 设置-修改时间
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUpdateTime(Short value) {
		this.setProperty(UpdateTimeProperty, value);
	}

	/**
	 * 实例号（版本） 属性
	 */
	@DbField(name = "LogInst", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> LogInstProperty = registerProperty("LogInst", Integer.class, MY_CLASS);

	/**
	 * 获取-实例号（版本）
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "LogInst")
	public final Integer getLogInst() {
		return this.getProperty(LogInstProperty);
	}

	/**
	 * 设置-实例号（版本）
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLogInst(Integer value) {
		this.setProperty(LogInstProperty, value);
	}

	/**
	 * 服务系列 属性
	 */
	@DbField(name = "Series", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> SeriesProperty = registerProperty("Series", Integer.class, MY_CLASS);

	/**
	 * 获取-服务系列
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Series")
	public final Integer getSeries() {
		return this.getProperty(SeriesProperty);
	}

	/**
	 * 设置-服务系列
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setSeries(Integer value) {
		this.setProperty(SeriesProperty, value);
	}

	/**
	 * 创建用户 属性
	 */
	@DbField(name = "Creator", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> CreateUserSignProperty = registerProperty("CreateUserSign",
			Integer.class, MY_CLASS);

	/**
	 * 获取-创建用户
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "CreateUserSign")
	public final Integer getCreateUserSign() {
		return this.getProperty(CreateUserSignProperty);
	}

	/**
	 * 设置-创建用户
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCreateUserSign(Integer value) {
		this.setProperty(CreateUserSignProperty, value);
	}

	/**
	 * 修改用户 属性
	 */
	@DbField(name = "Updator", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> UpdateUserSignProperty = registerProperty("UpdateUserSign",
			Integer.class, MY_CLASS);

	/**
	 * 获取-修改用户
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UpdateUserSign")
	public final Integer getUpdateUserSign() {
		return this.getProperty(UpdateUserSignProperty);
	}

	/**
	 * 设置-修改用户
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUpdateUserSign(Integer value) {
		this.setProperty(UpdateUserSignProperty, value);
	}

	/**
	 * 创建动作标识 属性
	 */
	@DbField(name = "CreateActId", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> CreateActionIdProperty = registerProperty("CreateActionId", String.class,
			MY_CLASS);

	/**
	 * 获取-创建动作标识
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "CreateActionId")
	public final String getCreateActionId() {
		return this.getProperty(CreateActionIdProperty);
	}

	/**
	 * 设置-创建动作标识
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCreateActionId(String value) {
		this.setProperty(CreateActionIdProperty, value);
	}

	/**
	 * 更新动作标识 属性
	 */
	@DbField(name = "UpdateActId", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> UpdateActionIdProperty = registerProperty("UpdateActionId", String.class,
			MY_CLASS);

	/**
	 * 获取-更新动作标识
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UpdateActionId")
	public final String getUpdateActionId() {
		return this.getProperty(UpdateActionIdProperty);
	}

	/**
	 * 设置-更新动作标识
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUpdateActionId(String value) {
		this.setProperty(UpdateActionIdProperty, value);
	}

	/**
	 * 审批状态 属性
	 */
	@DbField(name = "ApvlStatus", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emApprovalStatus> ApprovalStatusProperty = registerProperty("ApprovalStatus",
			emApprovalStatus.class, MY_CLASS);

	/**
	 * 获取-审批状态
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ApprovalStatus")
	public final emApprovalStatus getApprovalStatus() {
		return this.getProperty(ApprovalStatusProperty);
	}

	/**
	 * 设置-审批状态
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setApprovalStatus(emApprovalStatus value) {
		this.setProperty(ApprovalStatusProperty, value);
	}

	/**
	 * 数据所有者 属性
	 */
	@DbField(name = "DataOwner", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> DataOwnerProperty = registerProperty("DataOwner", Integer.class,
			MY_CLASS);

	/**
	 * 获取-数据所有者
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DataOwner")
	public final Integer getDataOwner() {
		return this.getProperty(DataOwnerProperty);
	}

	/**
	 * 设置-数据所有者
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDataOwner(Integer value) {
		this.setProperty(DataOwnerProperty, value);
	}

	/**
	 * 数据所属组织 属性
	 */
	@DbField(name = "OrgCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> OrganizationProperty = registerProperty("Organization", String.class,
			MY_CLASS);

	/**
	 * 获取-数据所属组织
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Organization")
	public final String getOrganization() {
		return this.getProperty(OrganizationProperty);
	}

	/**
	 * 设置-数据所属组织
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOrganization(String value) {
		this.setProperty(OrganizationProperty, value);
	}

	/**
	 * 电子邮件地址 属性
	 */
	@DbField(name = "eMail", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> eMailProperty = registerProperty("eMail", String.class, MY_CLASS);

	/**
	 * 获取-电子邮件地址
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "eMail")
	public final String geteMail() {
		return this.getProperty(eMailProperty);
	}

	/**
	 * 设置-电子邮件地址
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void seteMail(String value) {
		this.setProperty(eMailProperty, value);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();
		this.setObjectCode(BUSINESS_OBJECT_CODE);
		this.setActivated(emYesNo.Yes);
	}

}
