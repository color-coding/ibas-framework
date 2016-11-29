package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 标记删除，已被引用的对象标记删除
 * 
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOTagDeleted extends IBOReferenced {

    /**
     * 是否标记删除
     * 
     * @return
     */
    emYesNo getDeleted();

    /**
     * 设置-删除状态
     * 
     * @param value
     */
    void setDeleted(emYesNo value);
}
