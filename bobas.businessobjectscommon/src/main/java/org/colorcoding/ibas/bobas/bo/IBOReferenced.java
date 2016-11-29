package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 被引用的业务对象
 * 
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOReferenced {
    /**
     * 是否被引用
     * 
     * @return
     */
    emYesNo getReferenced();

    /**
     * 设置-引用状态
     * 
     * @param value
     */
    void setReferenced(emYesNo value);
}
