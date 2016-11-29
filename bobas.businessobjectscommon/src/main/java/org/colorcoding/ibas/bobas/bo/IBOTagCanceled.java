package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 标记取消，已被引用的对象不能取消
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBOTagCanceled extends IBOReferenced {

    /**
     * 是否取消
     * 
     * @return
     */
    emYesNo getCanceled();

    /**
     * 设置-取消状态
     * 
     * @param value
     */
    void setCanceled(emYesNo value);
}
