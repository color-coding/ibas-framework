package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;

/**
 * 单据行
 *
 * @author Niuren.Zhu
 *
 */
public interface IBODocumentLine extends IBusinessObject, IBOLine, IBOStorageTag {
    /**
     * 主要的主键名称
     */
    final static String MASTER_PRIMARY_KEY_NAME = "DocEntry";

    /**
     * 获取单据编号（主键）
     *
     * @return 单据编号
     */
    Integer getDocEntry();

    /**
     * 设置单据编号
     *
     * @param value 单据编号
     */
    void setDocEntry(Integer value);

    /**
     * 获取行编号（主键）
     *
     * @return 行编号
     */
    Integer getLineId();

    /**
     * 设置行编号
     *
     * @param value 行编号
     */
    void setLineId(Integer value);

    /**
     * 获取顺序号
     *
     * @return 顺序号
     */
    Integer getVisOrder();

    /**
     * 设置顺序号
     *
     * @param value 顺序号
     */
    void setVisOrder(Integer value);

    /**
     * 获取对象状态
     *
     * @return 对象状态
     */
    emBOStatus getStatus();

    /**
     * 设置对象状态
     *
     * @param value 对象状态
     */
    void setStatus(emBOStatus value);

    /**
     * 获取行状态
     *
     * @return 行状态
     */
    emDocumentStatus getLineStatus();

    /**
     * 设置行状态
     *
     * @param value 行状态
     */
    void setLineStatus(emDocumentStatus value);
}
