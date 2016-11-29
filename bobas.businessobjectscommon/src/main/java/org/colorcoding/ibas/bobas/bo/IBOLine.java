package org.colorcoding.ibas.bobas.bo;

/**
 * 行对象
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOLine extends IBusinessObject {
    /**
     * 主要的主键名称
     */
    final static String SECONDARY_PRIMARY_KEY_NAME = "LineId";

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
}
