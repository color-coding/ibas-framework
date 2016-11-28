package org.colorcoding.ibas.bobas.bo;

/**
 * 业务对象实例描述
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBODescription {
    /**
     * 获取实例描述
     * 
     * @param target
     *            目标
     * @return
     */
    String getDescription(Object target);
}
