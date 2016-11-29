package org.colorcoding.ibas.bobas.bo;

public interface IBOMasterDataLine extends IBusinessObject, IBOLine, IBOStorageTag {
    /**
     * 主要的主键名称
     */
    final static String MASTER_PRIMARY_KEY_NAME = "Code";

    /**
     * 获取-编码 主键
     * 
     * @return
     */
    String getCode();

    /**
     * 设置-编码 主键
     * 
     * @param value
     */
    void setCode(String value);

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
