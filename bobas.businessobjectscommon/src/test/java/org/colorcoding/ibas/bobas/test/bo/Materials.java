package org.colorcoding.ibas.bobas.test.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.mapping.BOCode;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 获取-物料主数据
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Materials")
@XmlRootElement(name = "Materials")
@BOCode(Materials.BUSINESS_OBJECT_CODE)
public class Materials extends BusinessObject<Materials> implements IMaterials {

	/**
	 * 序列化版本标记
	 */
	private static final long serialVersionUID = 5256857080069993005L;

	/**
	 * 当前类型
	 */
	private final static Class<?> MY_CLASS = Materials.class;

	/**
	 * 数据库表
	 */
	public final static String DB_TABLE_NAME = "CC_TT_OITM";

	/**
	 * 业务对象编码
	 */
	public final static String BUSINESS_OBJECT_CODE = "CC_TT_MATERIALS";

	/**
	 * 创建实例
	 * 
	 * @return 物料主数据实例
	 */
	public static IMaterials create() {
		return new Materials();
	}

	/**
	 * 物料编号 属性
	 */
	@DbField(name = "ItemCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = true)
	public final static IPropertyInfo<String> ItemCodeProperty = registerProperty("ItemCode", String.class, MY_CLASS);

	/**
	 * 获取-物料编号
	 * 
	 * @return 值
	 */
	@XmlElement(name = "ItemCode")
	public final String getItemCode() {
		return this.getProperty(ItemCodeProperty);
	}

	/**
	 * 设置-物料编号
	 * 
	 * @param value
	 *            值
	 */
	public final void setItemCode(String value) {
		this.setProperty(ItemCodeProperty, value);
	}

	/**
	 * 物料描述 属性
	 */
	@DbField(name = "ItemName", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> ItemDescriptionProperty = registerProperty("ItemDescription",
			String.class, MY_CLASS);

	/**
	 * 获取-物料描述
	 * 
	 * @return 值
	 */
	@XmlElement(name = "ItemDescription")
	public final String getItemDescription() {
		return this.getProperty(ItemDescriptionProperty);
	}

	/**
	 * 设置-物料描述
	 * 
	 * @param value
	 *            值
	 */
	public final void setItemDescription(String value) {
		this.setProperty(ItemDescriptionProperty, value);
	}

	/**
	 * 订单数量 属性
	 */
	@DbField(name = "OnOrder", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> OnOrderProperty = registerProperty("OnOrder", Decimal.class, MY_CLASS);

	/**
	 * 获取-订单数量
	 * 
	 * @return 值
	 */
	@XmlElement(name = "OnOrder")
	public final Decimal getOnOrder() {
		return this.getProperty(OnOrderProperty);
	}

	/**
	 * 设置-订单数量
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnOrder(Decimal value) {
		this.setProperty(OnOrderProperty, value);
	}

	/**
	 * 设置-订单数量
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnOrder(String value) {
		this.setOnOrder(new Decimal(value));
	}

	/**
	 * 设置-订单数量
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnOrder(int value) {
		this.setOnOrder(new Decimal(value));
	}

	/**
	 * 设置-订单数量
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnOrder(double value) {
		this.setOnOrder(new Decimal(value));
	}

	/**
	 * 库存 属性
	 */
	@DbField(name = "OnHand", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> OnHandProperty = registerProperty("OnHand", Decimal.class, MY_CLASS);

	/**
	 * 获取-库存
	 * 
	 * @return 值
	 */
	@XmlElement(name = "OnHand")
	public final Decimal getOnHand() {
		return this.getProperty(OnHandProperty);
	}

	/**
	 * 设置-库存
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnHand(Decimal value) {
		this.setProperty(OnHandProperty, value);
	}

	/**
	 * 设置-库存
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnHand(String value) {
		this.setOnHand(new Decimal(value));
	}

	/**
	 * 设置-库存
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnHand(int value) {
		this.setOnHand(new Decimal(value));
	}

	/**
	 * 设置-库存
	 * 
	 * @param value
	 *            值
	 */
	public final void setOnHand(double value) {
		this.setOnHand(new Decimal(value));
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
	public final void setObjectKey(Integer value) {
		this.setProperty(ObjectKeyProperty, value);
	}

	/**
	 * 类型 属性
	 */
	@DbField(name = "Object", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> ObjectCodeProperty = registerProperty("ObjectCode", String.class,
			MY_CLASS);

	/**
	 * 获取-类型
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ObjectCode")
	public final String getObjectCode() {
		return this.getProperty(ObjectCodeProperty);
	}

	/**
	 * 设置-类型
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setObjectCode(String value) {
		this.setProperty(ObjectCodeProperty, value);
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
	public final void setLogInst(Integer value) {
		this.setProperty(LogInstProperty, value);
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
	public final void setDataSource(String value) {
		this.setProperty(DataSourceProperty, value);
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
	public final void setDataOwner(Integer value) {
		this.setProperty(DataOwnerProperty, value);
	}

	/**
	 * 团队成员 属性
	 */
	@DbField(name = "TeamMembers", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> TeamMembersProperty = registerProperty("TeamMembers", String.class,
			MY_CLASS);

	/**
	 * 获取-团队成员
	 * 
	 * @return 值
	 */
	@XmlElement(name = "TeamMembers")
	public final String getTeamMembers() {
		return this.getProperty(TeamMembersProperty);
	}

	/**
	 * 设置-团队成员
	 * 
	 * @param value
	 *            值
	 */
	public final void setTeamMembers(String value) {
		this.setProperty(TeamMembersProperty, value);
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
	public final void setOrganization(String value) {
		this.setProperty(OrganizationProperty, value);
	}

	/**
	 * 已引用 属性
	 */
	@DbField(name = "Refed", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> ReferencedProperty = registerProperty("Referenced", emYesNo.class,
			MY_CLASS);

	/**
	 * 获取-已引用
	 * 
	 * @return 值
	 */
	@XmlElement(name = "Referenced")
	public final emYesNo getReferenced() {
		return this.getProperty(ReferencedProperty);
	}

	/**
	 * 设置-已引用
	 * 
	 * @param value
	 *            值
	 */
	public final void setReferenced(emYesNo value) {
		this.setProperty(ReferencedProperty, value);
	}

	/**
	 * 已删除 属性
	 */
	@DbField(name = "Deleted", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> DeletedProperty = registerProperty("Deleted", emYesNo.class, MY_CLASS);

	/**
	 * 获取-已删除
	 * 
	 * @return 值
	 */
	@XmlElement(name = "Deleted")
	public final emYesNo getDeleted() {
		return this.getProperty(DeletedProperty);
	}

	/**
	 * 设置-已删除
	 * 
	 * @param value
	 *            值
	 */
	public final void setDeleted(emYesNo value) {
		this.setProperty(DeletedProperty, value);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();// 基类初始化，不可去除
		this.setObjectCode(BUSINESS_OBJECT_CODE);

	}

}
