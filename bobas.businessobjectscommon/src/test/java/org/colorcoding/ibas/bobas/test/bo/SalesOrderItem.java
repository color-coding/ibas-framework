package org.colorcoding.ibas.bobas.test.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 获取-销售订单-行
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SalesOrderItem")
public class SalesOrderItem extends BusinessObject<SalesOrderItem> implements ISalesOrderItem, IBOUserFields {

	/**
	 * 序列化版本标记
	 */
	private static final long serialVersionUID = 5079118867992061656L;

	/**
	 * 当前类型
	 */
	private final static Class<?> MY_CLASS = SalesOrderItem.class;

	/**
	 * 数据库表
	 */
	public final static String DB_TABLE_NAME = "CC_TT_RDR1";

	/**
	 * 业务对象编码
	 */
	public final static String BUSINESS_OBJECT_CODE = "CC_TT_SALESORDER";

	/**
	 * 编码 属性
	 */
	@DbField(name = "DocEntry", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = true)
	public final static IPropertyInfo<Integer> DocEntryProperty = registerProperty("DocEntry", Integer.class, MY_CLASS);

	/**
	 * 获取-编码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DocEntry")
	public final Integer getDocEntry() {
		return this.getProperty(DocEntryProperty);
	}

	/**
	 * 设置-编码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDocEntry(Integer value) {
		this.setProperty(DocEntryProperty, value);
	}

	/**
	 * 行号 属性
	 */
	@DbField(name = "LineId", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = true)
	public final static IPropertyInfo<Integer> LineIdProperty = registerProperty("LineId", Integer.class, MY_CLASS);

	/**
	 * 获取-行号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "LineId")
	public final Integer getLineId() {
		return this.getProperty(LineIdProperty);
	}

	/**
	 * 设置-行号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineId(Integer value) {
		this.setProperty(LineIdProperty, value);
	}

	/**
	 * 显示顺序 属性
	 */
	@DbField(name = "VisOrder", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> VisOrderProperty = registerProperty("VisOrder", Integer.class, MY_CLASS);

	/**
	 * 获取-显示顺序
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "VisOrder")
	public final Integer getVisOrder() {
		return this.getProperty(VisOrderProperty);
	}

	/**
	 * 设置-显示顺序
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setVisOrder(Integer value) {
		this.setProperty(VisOrderProperty, value);
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
	 * 取消 属性
	 */
	@DbField(name = "Canceled", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> CanceledProperty = registerProperty("Canceled", emYesNo.class, MY_CLASS);

	/**
	 * 获取-取消
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Canceled")
	public final emYesNo getCanceled() {
		return this.getProperty(CanceledProperty);
	}

	/**
	 * 设置-取消
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCanceled(emYesNo value) {
		this.setProperty(CanceledProperty, value);
	}

	/**
	 * 状态 属性
	 */
	@DbField(name = "Status", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emBOStatus> StatusProperty = registerProperty("Status", emBOStatus.class,
			MY_CLASS);

	/**
	 * 获取-状态
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Status")
	public final emBOStatus getStatus() {
		return this.getProperty(StatusProperty);
	}

	/**
	 * 设置-状态
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setStatus(emBOStatus value) {
		this.setProperty(StatusProperty, value);
	}

	/**
	 * 单据状态 属性
	 */
	@DbField(name = "LineStatus", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emDocumentStatus> LineStatusProperty = registerProperty("LineStatus",
			emDocumentStatus.class, MY_CLASS);

	/**
	 * 获取-单据状态
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "LineStatus")
	public final emDocumentStatus getLineStatus() {
		return this.getProperty(LineStatusProperty);
	}

	/**
	 * 设置-单据状态
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineStatus(emDocumentStatus value) {
		this.setProperty(LineStatusProperty, value);
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
	 * 参考1 属性
	 */
	@DbField(name = "Ref1", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> Reference1Property = registerProperty("Reference1", String.class,
			MY_CLASS);

	/**
	 * 获取-参考1
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Reference1")
	public final String getReference1() {
		return this.getProperty(Reference1Property);
	}

	/**
	 * 设置-参考1
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setReference1(String value) {
		this.setProperty(Reference1Property, value);
	}

	/**
	 * 参考2 属性
	 */
	@DbField(name = "Ref2", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> Reference2Property = registerProperty("Reference2", String.class,
			MY_CLASS);

	/**
	 * 获取-参考2
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Reference2")
	public final String getReference2() {
		return this.getProperty(Reference2Property);
	}

	/**
	 * 设置-参考2
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setReference2(String value) {
		this.setProperty(Reference2Property, value);
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public final void setDeleted(emYesNo value) {
		this.setProperty(DeletedProperty, value);
	}

	/**
	 * 目标单据类型 属性
	 */
	@DbField(name = "TargetType", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> TargetDocumentTypeProperty = registerProperty("TargetDocumentType",
			String.class, MY_CLASS);

	/**
	 * 获取-目标单据类型
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "TargetDocumentType")
	public final String getTargetDocumentType() {
		return this.getProperty(TargetDocumentTypeProperty);
	}

	/**
	 * 设置-目标单据类型
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTargetDocumentType(String value) {
		this.setProperty(TargetDocumentTypeProperty, value);
	}

	/**
	 * 目标单据内部标识 属性
	 */
	@DbField(name = "TrgetEntry", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> TargetDocumentEntryProperty = registerProperty("TargetDocumentEntry",
			Integer.class, MY_CLASS);

	/**
	 * 获取-目标单据内部标识
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "TargetDocumentEntry")
	public final Integer getTargetDocumentEntry() {
		return this.getProperty(TargetDocumentEntryProperty);
	}

	/**
	 * 设置-目标单据内部标识
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTargetDocumentEntry(Integer value) {
		this.setProperty(TargetDocumentEntryProperty, value);
	}

	/**
	 * 基本单据参考 属性
	 */
	@DbField(name = "BaseRef", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> BaseReferenceProperty = registerProperty("BaseReference", String.class,
			MY_CLASS);

	/**
	 * 获取-基本单据参考
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "BaseReference")
	public final String getBaseReference() {
		return this.getProperty(BaseReferenceProperty);
	}

	/**
	 * 设置-基本单据参考
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBaseReference(String value) {
		this.setProperty(BaseReferenceProperty, value);
	}

	/**
	 * 基本单据类型 属性
	 */
	@DbField(name = "BaseType", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> BaseDocumentTypeProperty = registerProperty("BaseDocumentType",
			String.class, MY_CLASS);

	/**
	 * 获取-基本单据类型
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "BaseDocumentType")
	public final String getBaseDocumentType() {
		return this.getProperty(BaseDocumentTypeProperty);
	}

	/**
	 * 设置-基本单据类型
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBaseDocumentType(String value) {
		this.setProperty(BaseDocumentTypeProperty, value);
	}

	/**
	 * 基本单据内部标识 属性
	 */
	@DbField(name = "BaseEntry", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> BaseDocumentEntryProperty = registerProperty("BaseDocumentEntry",
			Integer.class, MY_CLASS);

	/**
	 * 获取-基本单据内部标识
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "BaseDocumentEntry")
	public final Integer getBaseDocumentEntry() {
		return this.getProperty(BaseDocumentEntryProperty);
	}

	/**
	 * 设置-基本单据内部标识
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBaseDocumentEntry(Integer value) {
		this.setProperty(BaseDocumentEntryProperty, value);
	}

	/**
	 * 基本凭证中的行编号 属性
	 */
	@DbField(name = "BaseLinNum", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> BaseDocumentLineIdProperty = registerProperty("BaseDocumentLineId",
			Integer.class, MY_CLASS);

	/**
	 * 获取-基本凭证中的行编号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "BaseDocumentLineId")
	public final Integer getBaseDocumentLineId() {
		return this.getProperty(BaseDocumentLineIdProperty);
	}

	/**
	 * 设置-基本凭证中的行编号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBaseDocumentLineId(Integer value) {
		this.setProperty(BaseDocumentLineIdProperty, value);
	}

	/**
	 * 物料编号 属性
	 */
	@DbField(name = "ItemCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> ItemCodeProperty = registerProperty("ItemCode", String.class, MY_CLASS);

	/**
	 * 获取-物料编号
	 * 
	 * @return 值
	 */
	@Override
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
	@Override
	public final void setItemCode(String value) {
		this.setProperty(ItemCodeProperty, value);
	}

	/**
	 * 物料/服务描述 属性
	 */
	@DbField(name = "Dscription", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> ItemDescriptionProperty = registerProperty("ItemDescription",
			String.class, MY_CLASS);

	/**
	 * 获取-物料/服务描述
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ItemDescription")
	public final String getItemDescription() {
		return this.getProperty(ItemDescriptionProperty);
	}

	/**
	 * 设置-物料/服务描述
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setItemDescription(String value) {
		this.setProperty(ItemDescriptionProperty, value);
	}

	/**
	 * 序号管理 属性
	 */
	@DbField(name = "ManSerNum", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> SerialNumberManagementProperty = registerProperty(
			"SerialNumberManagement", emYesNo.class, MY_CLASS);

	/**
	 * 获取-序号管理
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "SerialNumberManagement")
	public final emYesNo getSerialNumberManagement() {
		return this.getProperty(SerialNumberManagementProperty);
	}

	/**
	 * 设置-序号管理
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setSerialNumberManagement(emYesNo value) {
		this.setProperty(SerialNumberManagementProperty, value);
	}

	/**
	 * 管理批号 属性
	 */
	@DbField(name = "ManBtchNum", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> BatchNumberManagementProperty = registerProperty("BatchNumberManagement",
			emYesNo.class, MY_CLASS);

	/**
	 * 获取-管理批号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "BatchNumberManagement")
	public final emYesNo getBatchNumberManagement() {
		return this.getProperty(BatchNumberManagementProperty);
	}

	/**
	 * 设置-管理批号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBatchNumberManagement(emYesNo value) {
		this.setProperty(BatchNumberManagementProperty, value);
	}

	/**
	 * 服务卡号管理 属性
	 */
	@DbField(name = "ManServiceNum", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> ServiceNumberManagementProperty = registerProperty(
			"ServiceNumberManagement", emYesNo.class, MY_CLASS);

	/**
	 * 获取-服务卡号管理
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ServiceNumberManagement")
	public final emYesNo getServiceNumberManagement() {
		return this.getProperty(ServiceNumberManagementProperty);
	}

	/**
	 * 设置-服务卡号管理
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setServiceNumberManagement(emYesNo value) {
		this.setProperty(ServiceNumberManagementProperty, value);
	}

	/**
	 * 数量 属性
	 */
	@DbField(name = "Quantity", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> QuantityProperty = registerProperty("Quantity", Decimal.class, MY_CLASS);

	/**
	 * 获取-数量
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Quantity")
	public final Decimal getQuantity() {
		return this.getProperty(QuantityProperty);
	}

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setQuantity(Decimal value) {
		this.setProperty(QuantityProperty, value);
	}

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setQuantity(String value) {
		this.setQuantity(new Decimal(value));
	}

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setQuantity(int value) {
		this.setQuantity(new Decimal(value));
	}

	/**
	 * 设置-数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setQuantity(double value) {
		this.setQuantity(new Decimal(value));
	}

	/**
	 * 行交货日期 属性
	 */
	@DbField(name = "ShipDate", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<DateTime> DeliveryDateProperty = registerProperty("DeliveryDate", DateTime.class,
			MY_CLASS);

	/**
	 * 获取-行交货日期
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DeliveryDate")
	public final DateTime getDeliveryDate() {
		return this.getProperty(DeliveryDateProperty);
	}

	/**
	 * 设置-行交货日期
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDeliveryDate(DateTime value) {
		this.setProperty(DeliveryDateProperty, value);
	}

	/**
	 * 剩余未清数量 属性
	 */
	@DbField(name = "OpenQty", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> OpenQuantityProperty = registerProperty("OpenQuantity", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-剩余未清数量
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "OpenQuantity")
	public final Decimal getOpenQuantity() {
		return this.getProperty(OpenQuantityProperty);
	}

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenQuantity(Decimal value) {
		this.setProperty(OpenQuantityProperty, value);
	}

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenQuantity(String value) {
		this.setOpenQuantity(new Decimal(value));
	}

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenQuantity(int value) {
		this.setOpenQuantity(new Decimal(value));
	}

	/**
	 * 设置-剩余未清数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenQuantity(double value) {
		this.setOpenQuantity(new Decimal(value));
	}

	/**
	 * 单价 属性
	 */
	@DbField(name = "Price", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> PriceProperty = registerProperty("Price", Decimal.class, MY_CLASS);

	/**
	 * 获取-单价
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Price")
	public final Decimal getPrice() {
		return this.getProperty(PriceProperty);
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setPrice(Decimal value) {
		this.setProperty(PriceProperty, value);
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setPrice(String value) {
		this.setPrice(new Decimal(value));
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setPrice(int value) {
		this.setPrice(new Decimal(value));
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setPrice(double value) {
		this.setPrice(new Decimal(value));
	}

	/**
	 * 价格货币 属性
	 */
	@DbField(name = "Currency", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> PriceCurrencyProperty = registerProperty("PriceCurrency", String.class,
			MY_CLASS);

	/**
	 * 获取-价格货币
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "PriceCurrency")
	public final String getPriceCurrency() {
		return this.getProperty(PriceCurrencyProperty);
	}

	/**
	 * 设置-价格货币
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setPriceCurrency(String value) {
		this.setProperty(PriceCurrencyProperty, value);
	}

	/**
	 * 货币汇率 属性
	 */
	@DbField(name = "Rate", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> CurrencyRateProperty = registerProperty("CurrencyRate", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-货币汇率
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "CurrencyRate")
	public final Decimal getCurrencyRate() {
		return this.getProperty(CurrencyRateProperty);
	}

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCurrencyRate(Decimal value) {
		this.setProperty(CurrencyRateProperty, value);
	}

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCurrencyRate(String value) {
		this.setCurrencyRate(new Decimal(value));
	}

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCurrencyRate(int value) {
		this.setCurrencyRate(new Decimal(value));
	}

	/**
	 * 设置-货币汇率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setCurrencyRate(double value) {
		this.setCurrencyRate(new Decimal(value));
	}

	/**
	 * 每行折扣 % 属性
	 */
	@DbField(name = "DiscPrcnt", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> DiscountPerLineProperty = registerProperty("DiscountPerLine",
			Decimal.class, MY_CLASS);

	/**
	 * 获取-每行折扣 %
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DiscountPerLine")
	public final Decimal getDiscountPerLine() {
		return this.getProperty(DiscountPerLineProperty);
	}

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDiscountPerLine(Decimal value) {
		this.setProperty(DiscountPerLineProperty, value);
	}

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDiscountPerLine(String value) {
		this.setDiscountPerLine(new Decimal(value));
	}

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDiscountPerLine(int value) {
		this.setDiscountPerLine(new Decimal(value));
	}

	/**
	 * 设置-每行折扣 %
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDiscountPerLine(double value) {
		this.setDiscountPerLine(new Decimal(value));
	}

	/**
	 * 行总计 属性
	 */
	@DbField(name = "LineTotal", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> LineTotalProperty = registerProperty("LineTotal", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-行总计
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "LineTotal")
	public final Decimal getLineTotal() {
		return this.getProperty(LineTotalProperty);
	}

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineTotal(Decimal value) {
		this.setProperty(LineTotalProperty, value);
	}

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineTotal(String value) {
		this.setLineTotal(new Decimal(value));
	}

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineTotal(int value) {
		this.setLineTotal(new Decimal(value));
	}

	/**
	 * 设置-行总计
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineTotal(double value) {
		this.setLineTotal(new Decimal(value));
	}

	/**
	 * 未清金额 属性
	 */
	@DbField(name = "OpenSum", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> OpenAmountProperty = registerProperty("OpenAmount", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-未清金额
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "OpenAmount")
	public final Decimal getOpenAmount() {
		return this.getProperty(OpenAmountProperty);
	}

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenAmount(Decimal value) {
		this.setProperty(OpenAmountProperty, value);
	}

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenAmount(String value) {
		this.setOpenAmount(new Decimal(value));
	}

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenAmount(int value) {
		this.setOpenAmount(new Decimal(value));
	}

	/**
	 * 设置-未清金额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOpenAmount(double value) {
		this.setOpenAmount(new Decimal(value));
	}

	/**
	 * 供应商目录编号 属性
	 */
	@DbField(name = "VendorNum", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> VendorCatalogNumberProperty = registerProperty("VendorCatalogNumber",
			String.class, MY_CLASS);

	/**
	 * 获取-供应商目录编号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "VendorCatalogNumber")
	public final String getVendorCatalogNumber() {
		return this.getProperty(VendorCatalogNumberProperty);
	}

	/**
	 * 设置-供应商目录编号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setVendorCatalogNumber(String value) {
		this.setProperty(VendorCatalogNumberProperty, value);
	}

	/**
	 * 仓库代码 属性
	 */
	@DbField(name = "WhsCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> WarehouseProperty = registerProperty("Warehouse", String.class, MY_CLASS);

	/**
	 * 获取-仓库代码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "Warehouse")
	public final String getWarehouse() {
		return this.getProperty(WarehouseProperty);
	}

	/**
	 * 设置-仓库代码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setWarehouse(String value) {
		this.setProperty(WarehouseProperty, value);
	}

	/**
	 * 物料单基础数量 属性
	 */
	@DbField(name = "TreeBasisQty", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> TreeBasisQuantityProperty = registerProperty("TreeBasisQuantity",
			Decimal.class, MY_CLASS);

	/**
	 * 获取-物料单基础数量
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "TreeBasisQuantity")
	public final Decimal getTreeBasisQuantity() {
		return this.getProperty(TreeBasisQuantityProperty);
	}

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTreeBasisQuantity(Decimal value) {
		this.setProperty(TreeBasisQuantityProperty, value);
	}

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTreeBasisQuantity(String value) {
		this.setTreeBasisQuantity(new Decimal(value));
	}

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTreeBasisQuantity(int value) {
		this.setTreeBasisQuantity(new Decimal(value));
	}

	/**
	 * 设置-物料单基础数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTreeBasisQuantity(double value) {
		this.setTreeBasisQuantity(new Decimal(value));
	}

	/**
	 * 行标志号 属性
	 */
	@DbField(name = "LineSign", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> LineSignProperty = registerProperty("LineSign", String.class, MY_CLASS);

	/**
	 * 获取-行标志号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "LineSign")
	public final String getLineSign() {
		return this.getProperty(LineSignProperty);
	}

	/**
	 * 设置-行标志号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineSign(String value) {
		this.setProperty(LineSignProperty, value);
	}

	/**
	 * 父项行标志号 属性
	 */
	@DbField(name = "ParentSign", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> ParentLineSignProperty = registerProperty("ParentLineSign", String.class,
			MY_CLASS);

	/**
	 * 获取-父项行标志号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ParentLineSign")
	public final String getParentLineSign() {
		return this.getProperty(ParentLineSignProperty);
	}

	/**
	 * 设置-父项行标志号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setParentLineSign(String value) {
		this.setProperty(ParentLineSignProperty, value);
	}

	/**
	 * 科目代码 属性
	 */
	@DbField(name = "AcctCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> AccountCodeProperty = registerProperty("AccountCode", String.class,
			MY_CLASS);

	/**
	 * 获取-科目代码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "AccountCode")
	public final String getAccountCode() {
		return this.getProperty(AccountCodeProperty);
	}

	/**
	 * 设置-科目代码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setAccountCode(String value) {
		this.setProperty(AccountCodeProperty, value);
	}

	/**
	 * 毛利的基础价格 属性
	 */
	@DbField(name = "GrossBuyPr", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> BasePriceforGrossProfitProperty = registerProperty(
			"BasePriceforGrossProfit", Decimal.class, MY_CLASS);

	/**
	 * 获取-毛利的基础价格
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "BasePriceforGrossProfit")
	public final Decimal getBasePriceforGrossProfit() {
		return this.getProperty(BasePriceforGrossProfitProperty);
	}

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBasePriceforGrossProfit(Decimal value) {
		this.setProperty(BasePriceforGrossProfitProperty, value);
	}

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBasePriceforGrossProfit(String value) {
		this.setBasePriceforGrossProfit(new Decimal(value));
	}

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBasePriceforGrossProfit(int value) {
		this.setBasePriceforGrossProfit(new Decimal(value));
	}

	/**
	 * 设置-毛利的基础价格
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBasePriceforGrossProfit(double value) {
		this.setBasePriceforGrossProfit(new Decimal(value));
	}

	/**
	 * 单价 属性
	 */
	@DbField(name = "PriceBefDi", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> UnitPriceProperty = registerProperty("UnitPrice", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-单价
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "UnitPrice")
	public final Decimal getUnitPrice() {
		return this.getProperty(UnitPriceProperty);
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUnitPrice(Decimal value) {
		this.setProperty(UnitPriceProperty, value);
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUnitPrice(String value) {
		this.setUnitPrice(new Decimal(value));
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUnitPrice(int value) {
		this.setUnitPrice(new Decimal(value));
	}

	/**
	 * 设置-单价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setUnitPrice(double value) {
		this.setUnitPrice(new Decimal(value));
	}

	/**
	 * 库存计量单位 属性
	 */
	@DbField(name = "UseBaseUn", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> InventoryUoMProperty = registerProperty("InventoryUoM", String.class,
			MY_CLASS);

	/**
	 * 获取-库存计量单位
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "InventoryUoM")
	public final String getInventoryUoM() {
		return this.getProperty(InventoryUoMProperty);
	}

	/**
	 * 设置-库存计量单位
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setInventoryUoM(String value) {
		this.setProperty(InventoryUoMProperty, value);
	}

	/**
	 * 条形码 属性
	 */
	@DbField(name = "CodeBars", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> BarCodeProperty = registerProperty("BarCode", String.class, MY_CLASS);

	/**
	 * 获取-条形码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "BarCode")
	public final String getBarCode() {
		return this.getProperty(BarCodeProperty);
	}

	/**
	 * 设置-条形码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setBarCode(String value) {
		this.setProperty(BarCodeProperty, value);
	}

	/**
	 * 每行税率 属性
	 */
	@DbField(name = "VatPrcnt", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> TaxRateperLineProperty = registerProperty("TaxRateperLine",
			Decimal.class, MY_CLASS);

	/**
	 * 获取-每行税率
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "TaxRateperLine")
	public final Decimal getTaxRateperLine() {
		return this.getProperty(TaxRateperLineProperty);
	}

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTaxRateperLine(Decimal value) {
		this.setProperty(TaxRateperLineProperty, value);
	}

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTaxRateperLine(String value) {
		this.setTaxRateperLine(new Decimal(value));
	}

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTaxRateperLine(int value) {
		this.setTaxRateperLine(new Decimal(value));
	}

	/**
	 * 设置-每行税率
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTaxRateperLine(double value) {
		this.setTaxRateperLine(new Decimal(value));
	}

	/**
	 * 税定义 属性
	 */
	@DbField(name = "VatGroup", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> TaxDefinitionProperty = registerProperty("TaxDefinition", String.class,
			MY_CLASS);

	/**
	 * 获取-税定义
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "TaxDefinition")
	public final String getTaxDefinition() {
		return this.getProperty(TaxDefinitionProperty);
	}

	/**
	 * 设置-税定义
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTaxDefinition(String value) {
		this.setProperty(TaxDefinitionProperty, value);
	}

	/**
	 * 毛价 属性
	 */
	@DbField(name = "PriceAfVAT", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> GrossPriceProperty = registerProperty("GrossPrice", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-毛价
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "GrossPrice")
	public final Decimal getGrossPrice() {
		return this.getProperty(GrossPriceProperty);
	}

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossPrice(Decimal value) {
		this.setProperty(GrossPriceProperty, value);
	}

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossPrice(String value) {
		this.setGrossPrice(new Decimal(value));
	}

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossPrice(int value) {
		this.setGrossPrice(new Decimal(value));
	}

	/**
	 * 设置-毛价
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossPrice(double value) {
		this.setGrossPrice(new Decimal(value));
	}

	/**
	 * 总税收 - 行 属性
	 */
	@DbField(name = "VatSum", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> TotalTaxLineProperty = registerProperty("TotalTaxLine", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-总税收 - 行
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "TotalTaxLine")
	public final Decimal getTotalTaxLine() {
		return this.getProperty(TotalTaxLineProperty);
	}

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTotalTaxLine(Decimal value) {
		this.setProperty(TotalTaxLineProperty, value);
	}

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTotalTaxLine(String value) {
		this.setTotalTaxLine(new Decimal(value));
	}

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTotalTaxLine(int value) {
		this.setTotalTaxLine(new Decimal(value));
	}

	/**
	 * 设置-总税收 - 行
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setTotalTaxLine(double value) {
		this.setTotalTaxLine(new Decimal(value));
	}

	/**
	 * 行毛利 属性
	 */
	@DbField(name = "GrssProfit", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> LineGrossProfitProperty = registerProperty("LineGrossProfit",
			Decimal.class, MY_CLASS);

	/**
	 * 获取-行毛利
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "LineGrossProfit")
	public final Decimal getLineGrossProfit() {
		return this.getProperty(LineGrossProfitProperty);
	}

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineGrossProfit(Decimal value) {
		this.setProperty(LineGrossProfitProperty, value);
	}

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineGrossProfit(String value) {
		this.setLineGrossProfit(new Decimal(value));
	}

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineGrossProfit(int value) {
		this.setLineGrossProfit(new Decimal(value));
	}

	/**
	 * 设置-行毛利
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineGrossProfit(double value) {
		this.setLineGrossProfit(new Decimal(value));
	}

	/**
	 * 总额 属性
	 */
	@DbField(name = "Gtotal", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> GrossTotalProperty = registerProperty("GrossTotal", Decimal.class,
			MY_CLASS);

	/**
	 * 获取-总额
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "GrossTotal")
	public final Decimal getGrossTotal() {
		return this.getProperty(GrossTotalProperty);
	}

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossTotal(Decimal value) {
		this.setProperty(GrossTotalProperty, value);
	}

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossTotal(String value) {
		this.setGrossTotal(new Decimal(value));
	}

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossTotal(int value) {
		this.setGrossTotal(new Decimal(value));
	}

	/**
	 * 设置-总额
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setGrossTotal(double value) {
		this.setGrossTotal(new Decimal(value));
	}

	/**
	 * 已交货的数量 属性
	 */
	@DbField(name = "DelivrdQty", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Decimal> DeliveredQuantityProperty = registerProperty("DeliveredQuantity",
			Decimal.class, MY_CLASS);

	/**
	 * 获取-已交货的数量
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DeliveredQuantity")
	public final Decimal getDeliveredQuantity() {
		return this.getProperty(DeliveredQuantityProperty);
	}

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDeliveredQuantity(Decimal value) {
		this.setProperty(DeliveredQuantityProperty, value);
	}

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDeliveredQuantity(String value) {
		this.setDeliveredQuantity(new Decimal(value));
	}

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDeliveredQuantity(int value) {
		this.setDeliveredQuantity(new Decimal(value));
	}

	/**
	 * 设置-已交货的数量
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDeliveredQuantity(double value) {
		this.setDeliveredQuantity(new Decimal(value));
	}

	/**
	 * 已手动关闭行 属性
	 */
	@DbField(name = "LinManClsd", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<emYesNo> LineWasClosedManuallyProperty = registerProperty("LineWasClosedManually",
			emYesNo.class, MY_CLASS);

	/**
	 * 获取-已手动关闭行
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "LineWasClosedManually")
	public final emYesNo getLineWasClosedManually() {
		return this.getProperty(LineWasClosedManuallyProperty);
	}

	/**
	 * 设置-已手动关闭行
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setLineWasClosedManually(emYesNo value) {
		this.setProperty(LineWasClosedManuallyProperty, value);
	}

	/**
	 * 项目代码 属性
	 */
	@DbField(name = "Project", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> ProjectCodeProperty = registerProperty("ProjectCode", String.class,
			MY_CLASS);

	/**
	 * 获取-项目代码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "ProjectCode")
	public final String getProjectCode() {
		return this.getProperty(ProjectCodeProperty);
	}

	/**
	 * 设置-项目代码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setProjectCode(String value) {
		this.setProperty(ProjectCodeProperty, value);
	}

	/**
	 * 分配规则 属性
	 */
	@DbField(name = "OcrCode1", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> DistributionRule1Property = registerProperty("DistributionRule1",
			String.class, MY_CLASS);

	/**
	 * 获取-分配规则
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DistributionRule1")
	public final String getDistributionRule1() {
		return this.getProperty(DistributionRule1Property);
	}

	/**
	 * 设置-分配规则
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDistributionRule1(String value) {
		this.setProperty(DistributionRule1Property, value);
	}

	/**
	 * 分配规则2 属性
	 */
	@DbField(name = "OcrCode2", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> DistributionRule2Property = registerProperty("DistributionRule2",
			String.class, MY_CLASS);

	/**
	 * 获取-分配规则2
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DistributionRule2")
	public final String getDistributionRule2() {
		return this.getProperty(DistributionRule2Property);
	}

	/**
	 * 设置-分配规则2
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDistributionRule2(String value) {
		this.setProperty(DistributionRule2Property, value);
	}

	/**
	 * 分配规则3 属性
	 */
	@DbField(name = "OcrCode3", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> DistributionRule3Property = registerProperty("DistributionRule3",
			String.class, MY_CLASS);

	/**
	 * 获取-分配规则3
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DistributionRule3")
	public final String getDistributionRule3() {
		return this.getProperty(DistributionRule3Property);
	}

	/**
	 * 设置-分配规则3
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDistributionRule3(String value) {
		this.setProperty(DistributionRule3Property, value);
	}

	/**
	 * 分配规则4 属性
	 */
	@DbField(name = "OcrCode4", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> DistributionRule4Property = registerProperty("DistributionRule4",
			String.class, MY_CLASS);

	/**
	 * 获取-分配规则4
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DistributionRule4")
	public final String getDistributionRule4() {
		return this.getProperty(DistributionRule4Property);
	}

	/**
	 * 设置-分配规则4
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDistributionRule4(String value) {
		this.setProperty(DistributionRule4Property, value);
	}

	/**
	 * 分配规则5 属性
	 */
	@DbField(name = "OcrCode5", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> DistributionRule5Property = registerProperty("DistributionRule5",
			String.class, MY_CLASS);

	/**
	 * 获取-分配规则5
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "DistributionRule5")
	public final String getDistributionRule5() {
		return this.getProperty(DistributionRule5Property);
	}

	/**
	 * 设置-分配规则5
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setDistributionRule5(String value) {
		this.setProperty(DistributionRule5Property, value);
	}

	/**
	 * 原始凭证类型 属性
	 */
	@DbField(name = "BsDocType", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> OriginalDocumentTypeProperty = registerProperty("OriginalDocumentType",
			String.class, MY_CLASS);

	/**
	 * 获取-原始凭证类型
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "OriginalDocumentType")
	public final String getOriginalDocumentType() {
		return this.getProperty(OriginalDocumentTypeProperty);
	}

	/**
	 * 设置-原始凭证类型
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOriginalDocumentType(String value) {
		this.setProperty(OriginalDocumentTypeProperty, value);
	}

	/**
	 * 原始凭证代码 属性
	 */
	@DbField(name = "BsDocEntry", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> OriginalDocumentEntryProperty = registerProperty("OriginalDocumentEntry",
			Integer.class, MY_CLASS);

	/**
	 * 获取-原始凭证代码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "OriginalDocumentEntry")
	public final Integer getOriginalDocumentEntry() {
		return this.getProperty(OriginalDocumentEntryProperty);
	}

	/**
	 * 设置-原始凭证代码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOriginalDocumentEntry(Integer value) {
		this.setProperty(OriginalDocumentEntryProperty, value);
	}

	/**
	 * 原始凭证中的行编号 属性
	 */
	@DbField(name = "BsDocLine", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> OriginalDocumentLineIdProperty = registerProperty(
			"OriginalDocumentLineId", Integer.class, MY_CLASS);

	/**
	 * 获取-原始凭证中的行编号
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "OriginalDocumentLineId")
	public final Integer getOriginalDocumentLineId() {
		return this.getProperty(OriginalDocumentLineIdProperty);
	}

	/**
	 * 设置-原始凭证中的行编号
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setOriginalDocumentLineId(Integer value) {
		this.setProperty(OriginalDocumentLineIdProperty, value);
	}

	/**
	 * 毛利价格清单 属性
	 */
	@DbField(name = "GrossBase", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<Integer> PriceListforGrossProfitProperty = registerProperty(
			"PriceListforGrossProfit", Integer.class, MY_CLASS);

	/**
	 * 获取-毛利价格清单
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "PriceListforGrossProfit")
	public final Integer getPriceListforGrossProfit() {
		return this.getProperty(PriceListforGrossProfitProperty);
	}

	/**
	 * 设置-毛利价格清单
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setPriceListforGrossProfit(Integer value) {
		this.setProperty(PriceListforGrossProfitProperty, value);
	}

	/**
	 * 促销活动代码 属性
	 */
	@DbField(name = "PrmtsCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
	public final static IPropertyInfo<String> PromotionCodeProperty = registerProperty("PromotionCode", String.class,
			MY_CLASS);

	/**
	 * 获取-促销活动代码
	 * 
	 * @return 值
	 */
	@Override
	@XmlElement(name = "PromotionCode")
	public final String getPromotionCode() {
		return this.getProperty(PromotionCodeProperty);
	}

	/**
	 * 设置-促销活动代码
	 * 
	 * @param value
	 *            值
	 */
	@Override
	public final void setPromotionCode(String value) {
		this.setProperty(PromotionCodeProperty, value);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();
		this.setObjectCode(BUSINESS_OBJECT_CODE);

	}

}
