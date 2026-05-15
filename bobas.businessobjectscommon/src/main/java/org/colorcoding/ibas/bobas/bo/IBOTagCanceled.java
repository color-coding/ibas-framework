package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 标记取消，已被引用的对象不能取消
 *
 * @author Niuren.Zhu
 *
 */
public interface IBOTagCanceled extends IBOTagReferenced {

    /**
     * 获取取消状态
     *
     * @return 是否取消
     */
    emYesNo getCanceled();

    /**
     * 设置取消状态
     *
     * @param value 取消状态
     */
    void setCanceled(emYesNo value);
}
