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
     * 获取-单据编号 主键
     * 
     * @return
     */
    Integer getDocEntry();

    /**
     * 设置-单据编号 主键
     * 
     * @param value
     */
    void setDocEntry(Integer value);

    /**
     * 获取-行编号 主键
     * 
     * @return
     */
    Integer getLineId();

    /**
     * 设置-行编号 主键
     * 
     * @param value
     */
    void setLineId(Integer value);

    /**
     * 获取-顺序号
     * 
     * @return
     */
    Integer getVisOrder();

    /**
     * 设置-顺序号
     * 
     * @param value
     */
    void setVisOrder(Integer value);

    /**
     * 获取-状态
     * 
     * @return
     */
    emBOStatus getStatus();

    /**
     * 设置-状态
     * 
     * @param value
     */
    void setStatus(emBOStatus value);

    /**
     * 获取-单据状态
     * 
     * @return
     */
    emDocumentStatus getLineStatus();

    /**
     * 设置-单据状态
     * 
     * @param value
     */
    void setLineStatus(emDocumentStatus value);
}
