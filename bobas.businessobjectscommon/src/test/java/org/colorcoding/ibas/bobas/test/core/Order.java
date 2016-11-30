package org.colorcoding.ibas.bobas.test.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.colorcoding.ibas.bobas.core.BusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

@XmlRootElement(name = "Order", namespace = "httpL//ibas.club/bobas/test")
public class Order extends BusinessObjectBase<Order> {
    /**
     * 
     */
    private static final long serialVersionUID = 4771656989360317386L;

    /**
     * 当前类型 java的泛型是擦除机制，不能在运行期间获取到泛型信息
     */
    private final static Class<?> MY_CLASS = Order.class;
    /**
     * 数据库表
     */
    public final static String DB_TABLE_NAME = "CC_SM_OPOR";
    /**
     * 数据库字段映射
     */
    @DbField(name = "DocEntry", type = DbFieldType.db_Numeric, table = DB_TABLE_NAME, primaryKey = true)
    /**
     * 依赖属性声明
     */
    public final static IPropertyInfo<Integer> DocEntryProperty = registerProperty("DocEntry", Integer.class, MY_CLASS);

    /**
     * 序列化标记
     */
    @XmlElement(name = "DocEntry")
    public final Integer getDocEntry() {
        return this.getProperty(DocEntryProperty);
    }

    /**
     * 
     * @param value
     */
    public final void setDocEntry(Integer value) {
        this.setProperty(DocEntryProperty, value);
    }

    @DbField(name = "Suppler", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<String> SupplerProperty = registerProperty("Suppler", String.class, MY_CLASS);

    /**
     * 供应商
     * 
     * @return
     */
    @XmlElement(name = "Suppler")
    public final String getSuppler() {
        return this.getProperty(SupplerProperty);
    }

    public final void setSuppler(String value) {
        this.setProperty(SupplerProperty, value);
    }

    @DbField(name = "DueDate", type = DbFieldType.db_Date, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<DateTime> DueDateProperty = registerProperty("DueDate", DateTime.class, MY_CLASS);

    @XmlElement(name = "DueDate")
    public final DateTime getDueDate() {
        return this.getProperty(DueDateProperty);
    }

    public final void setDueDate(DateTime value) {
        this.setProperty(DueDateProperty, value);
    }

    @DbField(name = "DocStatus", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<emDocumentStatus> DocumentStatusProperty = registerProperty("DocumentStatus",
            emDocumentStatus.class, MY_CLASS);

    @XmlElement(name = "DocumentStatus")
    public final emDocumentStatus getDocumentStatus() {
        return this.getProperty(DocumentStatusProperty);
    }

    public final void setDocumentStatus(emDocumentStatus value) {
        this.setProperty(DocumentStatusProperty, value);
    }

    @DbField(name = "Activted", type = DbFieldType.db_Alphanumeric, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<Boolean> ActivtedProperty = registerProperty("Activted", Boolean.class, MY_CLASS);

    @XmlElement(name = "Activted")
    public final Boolean getActivted() {
        return this.getProperty(ActivtedProperty);
    }

    public final void setActivted(Boolean value) {
        this.setProperty(ActivtedProperty, value);
    }

    @DbField(name = "DocTotal", type = DbFieldType.db_Decimal, table = DB_TABLE_NAME, primaryKey = false)
    public final static IPropertyInfo<Decimal> DocumentTotalProperty = registerProperty("DocumentTotal", Decimal.class,
            MY_CLASS);

    @XmlElement(name = "DocumentTotal")
    public final Decimal getDocumentTotal() {
        return this.getProperty(DocumentTotalProperty);
    }

    public final void setDocumentTotal(Decimal value) {
        this.setProperty(DocumentTotalProperty, value);
    }

    public String toString(String type) {
        return null;
    }

    public String getSchema(String type) {
        return null;
    }

}
