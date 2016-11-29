package org.colorcoding.ibas.bobas.test.logics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.mapping.BOCode;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.rules.IBusinessRule;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleMaxLength;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleMinValue;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleRequired;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleRequiredElements;

/**
 * 获取-采购订单
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "PurchaseOrder")
@XmlRootElement(name = "PurchaseOrder")
@BOCode(PurchaseOrder.BUSINESS_OBJECT_CODE)
public class PurchaseOrder extends BusinessObject<PurchaseOrder> implements IBODocument, IApprovalData, IBOTagCanceled {

    /**
     * 序列化版本标记
     */
    private static final long serialVersionUID = 5719902879783057081L;

    /**
     * 当前类型
     */
    private final static Class<?> MY_CLASS = PurchaseOrder.class;

    /**
     * 数据库表
     */
    public final static String DB_TABLE_NAME = "CC_TT_ORDR";

    /**
     * 业务对象编码
     */
    public final static String BUSINESS_OBJECT_CODE = "CC_TT_SALESORDER";

    /**
     * 凭证编号 属性
     */
    @DbField(name = "DocEntry", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = true)
    public final static IPropertyInfo<Integer> DocEntryProperty = registerProperty("DocEntry", Integer.class, MY_CLASS);

    /**
     * 获取-凭证编号
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "DocEntry")
    public final Integer getDocEntry() {
        return this.getProperty(DocEntryProperty);
    }

    /**
     * 设置-凭证编号
     * 
     * @param value
     *            值
     */
    @Override
    public final void setDocEntry(Integer value) {
        this.setProperty(DocEntryProperty, value);
    }

    /**
     * 期间编号 属性
     */
    @DbField(name = "DocNum", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<Integer> DocNumProperty = registerProperty("DocNum", Integer.class, MY_CLASS);

    /**
     * 获取-期间编号
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "DocNum")
    public final Integer getDocNum() {
        return this.getProperty(DocNumProperty);
    }

    /**
     * 设置-期间编号
     * 
     * @param value
     *            值
     */
    @Override
    public final void setDocNum(Integer value) {
        this.setProperty(DocNumProperty, value);
    }

    /**
     * 期间 属性
     */
    @DbField(name = "Period", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<Integer> PeriodProperty = registerProperty("Period", Integer.class, MY_CLASS);

    /**
     * 获取-期间
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "Period")
    public final Integer getPeriod() {
        return this.getProperty(PeriodProperty);
    }

    /**
     * 设置-期间
     * 
     * @param value
     *            值
     */
    @Override
    public final void setPeriod(Integer value) {
        this.setProperty(PeriodProperty, value);
    }

    /**
     * Instance 属性
     */
    @DbField(name = "Instance", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<Integer> InstanceProperty = registerProperty("Instance", Integer.class, MY_CLASS);

    /**
     * 获取-Instance
     * 
     * @return 值
     */
    @XmlElement(name = "Instance")
    public final Integer getInstance() {
        return this.getProperty(InstanceProperty);
    }

    /**
     * 设置-Instance
     * 
     * @param value
     *            值
     */
    public final void setInstance(Integer value) {
        this.setProperty(InstanceProperty, value);
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
     * 手写 属性
     */
    @DbField(name = "Handwrtten", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<emYesNo> HandwrittenProperty = registerProperty("Handwritten", emYesNo.class,
            MY_CLASS);

    /**
     * 获取-手写
     * 
     * @return 值
     */
    @XmlElement(name = "Handwritten")
    public final emYesNo getHandwritten() {
        return this.getProperty(HandwrittenProperty);
    }

    /**
     * 设置-手写
     * 
     * @param value
     *            值
     */
    public final void setHandwritten(emYesNo value) {
        this.setProperty(HandwrittenProperty, value);
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
     * 引用 属性
     */
    @DbField(name = "Refed", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<emYesNo> ReferencedProperty = registerProperty("Referenced", emYesNo.class,
            MY_CLASS);

    /**
     * 获取-引用
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "Referenced")
    public final emYesNo getReferenced() {
        return this.getProperty(ReferencedProperty);
    }

    /**
     * 设置-引用
     * 
     * @param value
     *            值
     */
    @Override
    public final void setReferenced(emYesNo value) {
        this.setProperty(ReferencedProperty, value);
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
     * 用户 属性
     */
    @DbField(name = "UserSign", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<Integer> UserSignProperty = registerProperty("UserSign", Integer.class, MY_CLASS);

    /**
     * 获取-用户
     * 
     * @return 值
     */
    @XmlElement(name = "UserSign")
    public final Integer getUserSign() {
        return this.getProperty(UserSignProperty);
    }

    /**
     * 设置-用户
     * 
     * @param value
     *            值
     */
    public final void setUserSign(Integer value) {
        this.setProperty(UserSignProperty, value);
    }

    /**
     * 是否结转 属性
     */
    @DbField(name = "Transfered", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<emYesNo> TransferedProperty = registerProperty("Transfered", emYesNo.class,
            MY_CLASS);

    /**
     * 获取-是否结转
     * 
     * @return 值
     */
    @XmlElement(name = "Transfered")
    public final emYesNo getTransfered() {
        return this.getProperty(TransferedProperty);
    }

    /**
     * 设置-是否结转
     * 
     * @param value
     *            值
     */
    public final void setTransfered(emYesNo value) {
        this.setProperty(TransferedProperty, value);
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
     * 单据状态 属性
     */
    @DbField(name = "DocStatus", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<emDocumentStatus> DocumentStatusProperty = registerProperty("DocumentStatus",
            emDocumentStatus.class, MY_CLASS);

    /**
     * 获取-单据状态
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "DocumentStatus")
    public final emDocumentStatus getDocumentStatus() {
        return this.getProperty(DocumentStatusProperty);
    }

    /**
     * 设置-单据状态
     * 
     * @param value
     *            值
     */
    @Override
    public final void setDocumentStatus(emDocumentStatus value) {
        this.setProperty(DocumentStatusProperty, value);
    }

    /**
     * 过账日期 属性
     */
    @DbField(name = "DocDate", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<DateTime> PostingDateProperty = registerProperty("PostingDate", DateTime.class,
            MY_CLASS);

    /**
     * 获取-过账日期
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "PostingDate")
    public final DateTime getPostingDate() {
        return this.getProperty(PostingDateProperty);
    }

    /**
     * 设置-过账日期
     * 
     * @param value
     *            值
     */
    @Override
    public final void setPostingDate(DateTime value) {
        this.setProperty(PostingDateProperty, value);
    }

    /**
     * 到期日 属性
     */
    @DbField(name = "DocDueDate", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<DateTime> DeliveryDateProperty = registerProperty("DeliveryDate", DateTime.class,
            MY_CLASS);

    /**
     * 获取-到期日
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "DeliveryDate")
    public final DateTime getDeliveryDate() {
        return this.getProperty(DeliveryDateProperty);
    }

    /**
     * 设置-到期日
     * 
     * @param value
     *            值
     */
    @Override
    public final void setDeliveryDate(DateTime value) {
        this.setProperty(DeliveryDateProperty, value);
    }

    /**
     * 凭证日期 属性
     */
    @DbField(name = "TaxDate", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<DateTime> DocumentDateProperty = registerProperty("DocumentDate", DateTime.class,
            MY_CLASS);

    /**
     * 获取-凭证日期
     * 
     * @return 值
     */
    @Override
    @XmlElement(name = "DocumentDate")
    public final DateTime getDocumentDate() {
        return this.getProperty(DocumentDateProperty);
    }

    /**
     * 设置-凭证日期
     * 
     * @param value
     *            值
     */
    @Override
    public final void setDocumentDate(DateTime value) {
        this.setProperty(DocumentDateProperty, value);
    }

    /**
     * 客户代码 属性
     */
    @DbField(name = "CardCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<String> CustomerCodeProperty = registerProperty("CustomerCode", String.class,
            MY_CLASS);

    /**
     * 获取-客户代码
     * 
     * @return 值
     */
    @XmlElement(name = "CustomerCode")
    public final String getCustomerCode() {
        return this.getProperty(CustomerCodeProperty);
    }

    /**
     * 设置-客户代码
     * 
     * @param value
     *            值
     */
    public final void setCustomerCode(String value) {
        this.setProperty(CustomerCodeProperty, value);
    }

    /**
     * 客户名称 属性
     */
    @DbField(name = "CardName", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<String> CustomerNameProperty = registerProperty("CustomerName", String.class,
            MY_CLASS);

    /**
     * 获取-客户名称
     * 
     * @return 值
     */
    @XmlElement(name = "CustomerName")
    public final String getCustomerName() {
        return this.getProperty(CustomerNameProperty);
    }

    /**
     * 设置-客户名称
     * 
     * @param value
     *            值
     */
    public final void setCustomerName(String value) {
        this.setProperty(CustomerNameProperty, value);
    }

    /**
     * 单据总计 属性
     */
    @DbField(name = "DocTotal", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<Decimal> DocumentTotalProperty = registerProperty("DocumentTotal", Decimal.class,
            MY_CLASS);

    /**
     * 获取-单据总计
     * 
     * @return 值
     */
    @XmlElement(name = "DocumentTotal")
    public final Decimal getDocumentTotal() {
        return this.getProperty(DocumentTotalProperty);
    }

    /**
     * 设置-单据总计
     * 
     * @param value
     *            值
     */
    public final void setDocumentTotal(Decimal value) {
        this.setProperty(DocumentTotalProperty, value);
    }

    /**
     * 设置-单据总计
     * 
     * @param value
     *            值
     */
    public final void setDocumentTotal(String value) {
        this.setDocumentTotal(new Decimal(value));
    }

    /**
     * 设置-单据总计
     * 
     * @param value
     *            值
     */
    public final void setDocumentTotal(int value) {
        this.setDocumentTotal(new Decimal(value));
    }

    /**
     * 设置-单据总计
     * 
     * @param value
     *            值
     */
    public final void setDocumentTotal(double value) {
        this.setDocumentTotal(new Decimal(value));
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
     * 销售订单-行的集合属性
     * 
     */
    public final static IPropertyInfo<PurchaseOrderItems> PurchaseOrderItemsProperty = registerProperty(
            "PurchaseOrderItems", PurchaseOrderItems.class, MY_CLASS);

    /**
     * 获取-采购订单-行集合
     * 
     * @return 值
     */
    @XmlElementWrapper(name = "PurchaseOrderItems")
    @XmlElement(name = "PurchaseOrderItem", type = PurchaseOrderItem.class, required = true)
    public PurchaseOrderItems getPurchaseOrderItems() {
        return this.getProperty(PurchaseOrderItemsProperty);
    }

    /**
     * 设置-采购订单-行集合
     * 
     * @param value
     *            值
     */
    public final void setPurchaseOrderItems(PurchaseOrderItems value) {
        this.setProperty(PurchaseOrderItemsProperty, value);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initialize() {
        super.initialize();
        this.setPurchaseOrderItems(new PurchaseOrderItems(this));
        this.setObjectCode(BUSINESS_OBJECT_CODE);
        this.setPostingDate(DateTime.getToday());
        this.setDocumentDate(DateTime.getToday());
        this.setDeliveryDate(DateTime.getToday());
        this.setDocumentStatus(emDocumentStatus.Released);

    }

    @Override
    protected IBusinessRule[] registerRules() {
        return new IBusinessRule[] { // 注册的业务规则
                new BusinessRuleRequired(CustomerCodeProperty), // 要求有值
                new BusinessRuleRequiredElements(PurchaseOrderItemsProperty), // 要求有元素
                new BusinessRuleMaxLength(20, CustomerCodeProperty), // 不能超过长度
                new BusinessRuleMinValue<Decimal>(Decimal.ZERO, DocumentTotalProperty),// 不能低于0
        };
    }
}
