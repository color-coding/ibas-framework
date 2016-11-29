package org.colorcoding.ibas.bobas.test.logics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.rules.IBusinessRule;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleMaxLength;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleMaxValue;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleMinValue;
import org.colorcoding.ibas.bobas.rules.common.BusinessRuleRequired;

/**
 * 获取-采购订单-行
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "PurchaseOrderItem")
public class PurchaseOrderItem extends BusinessObject<PurchaseOrderItem>
        implements IBODocumentLine, IMaterialsOrderQuantityContract, IMaterialsJournalContract, IBOTagCanceled {

    /**
     * 序列化版本标记
     */
    private static final long serialVersionUID = 5079118867992061656L;

    /**
     * 当前类型
     */
    private final static Class<?> MY_CLASS = PurchaseOrderItem.class;

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
     * 物料编号 属性
     */
    @DbField(name = "ItemCode", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
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
    public final void setItemDescription(String value) {
        this.setProperty(ItemDescriptionProperty, value);
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
    public final void setQuantity(Decimal value) {
        this.setProperty(QuantityProperty, value);
    }

    /**
     * 设置-数量
     * 
     * @param value
     *            值
     */
    public final void setQuantity(String value) {
        this.setQuantity(new Decimal(value));
    }

    /**
     * 设置-数量
     * 
     * @param value
     *            值
     */
    public final void setQuantity(int value) {
        this.setQuantity(new Decimal(value));
    }

    /**
     * 设置-数量
     * 
     * @param value
     *            值
     */
    public final void setQuantity(double value) {
        this.setQuantity(new Decimal(value));
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
    public final void setPrice(Decimal value) {
        this.setProperty(PriceProperty, value);
    }

    /**
     * 设置-单价
     * 
     * @param value
     *            值
     */
    public final void setPrice(String value) {
        this.setPrice(new Decimal(value));
    }

    /**
     * 设置-单价
     * 
     * @param value
     *            值
     */
    public final void setPrice(int value) {
        this.setPrice(new Decimal(value));
    }

    /**
     * 设置-单价
     * 
     * @param value
     *            值
     */
    public final void setPrice(double value) {
        this.setPrice(new Decimal(value));
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
    public final void setLineTotal(Decimal value) {
        this.setProperty(LineTotalProperty, value);
    }

    /**
     * 设置-行总计
     * 
     * @param value
     *            值
     */
    public final void setLineTotal(String value) {
        this.setLineTotal(new Decimal(value));
    }

    /**
     * 设置-行总计
     * 
     * @param value
     *            值
     */
    public final void setLineTotal(int value) {
        this.setLineTotal(new Decimal(value));
    }

    /**
     * 设置-行总计
     * 
     * @param value
     *            值
     */
    public final void setLineTotal(double value) {
        this.setLineTotal(new Decimal(value));
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initialize() {
        super.initialize();
        this.setObjectCode(BUSINESS_OBJECT_CODE);
        this.setLineStatus(emDocumentStatus.Released);
    }

    @Override
    protected IBusinessRule[] registerRules() {
        return new IBusinessRule[] { // 注册的业务规则
                new BusinessRuleRequired(ItemCodeProperty), // 要求有值
                new BusinessRuleMaxLength(20, ItemCodeProperty), // 不能超过长度
                new BusinessRuleMinValue<Decimal>(Decimal.ZERO, QuantityProperty, PriceProperty, LineTotalProperty), // 不能低于0
                new BusinessRuleMaxValue<Integer>(100, LineIdProperty), // 不能大于100
                new BusinessRuleCalculateTotals(PriceProperty, QuantityProperty, LineTotalProperty), // 计算总计
                new BusinessRuleCalculatePrice(LineTotalProperty, QuantityProperty, PriceProperty),// 计算价格
        };
    }

    // --IMaterialsJournalContract
    public String getDocumentType() {
        return this.getObjectCode();
    }

    public Integer getDocumentEntry() {
        return this.getDocEntry();
    }

    public Integer getDocumentLineId() {
        return this.getLineId();
    }

}
