package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 被引用的业务对象
 *
 * @author Niuren.Zhu
 *
 */
public interface IBOTagReferenced {
    /**
     * 获取引用状态
     *
     * @return 是否被引用
     */
    emYesNo getReferenced();

    /**
     * 设置引用状态
     *
     * @param value 引用状态
     */
    void setReferenced(emYesNo value);
}
