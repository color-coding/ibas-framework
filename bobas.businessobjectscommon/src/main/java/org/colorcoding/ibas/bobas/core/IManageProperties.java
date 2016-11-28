package org.colorcoding.ibas.bobas.core;

/**
 * 管理属性
 * 
 * @author Niuren.Zhu
 *
 */
public interface IManageProperties {
    /**
     * 存在管理的属性
     * 
     * @return true;false
     */
    boolean hasManagedProperties();

    /**
     * 属性是否存在
     * 
     * @param property
     *            属性
     * @return true;false
     */
    boolean existProperty(IPropertyInfo<?> property);

    /**
     * 获取属性的值
     * 
     * @param property
     *            属性
     * @return 值
     */
    <P> P getProperty(IPropertyInfo<P> property);

    /**
     * 设置属性的值
     * 
     * @param property
     *            属性
     * @param value
     *            值
     */
    <P> void setProperty(IPropertyInfo<P> property, P value);

    /**
     * 获取管理的所有属性
     * 
     * @return 属性数组
     */
    IPropertyInfo<?>[] getManagedProperties();

}
