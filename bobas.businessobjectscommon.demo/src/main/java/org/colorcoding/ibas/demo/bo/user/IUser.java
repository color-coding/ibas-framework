package org.colorcoding.ibas.demo.bo.user;

import java.math.*;

import org.colorcoding.ibas.bobas.bo.*;
import org.colorcoding.ibas.bobas.data.*;

/**
* 用户 接口
* 
*/
public interface IUser extends IBOSimple {

    /**
    * 获取-编码
    * 
    * @return 值
    */
    String getUserCode();

    /**
    * 设置-编码
    * 
    * @param value 值
    */
    void setUserCode(String value);


    /**
    * 获取-名称
    * 
    * @return 值
    */
    String getUserName();

    /**
    * 设置-名称
    * 
    * @param value 值
    */
    void setUserName(String value);


    /**
    * 获取-用户密码
    * 
    * @return 值
    */
    String getUserPassword();

    /**
    * 设置-用户密码
    * 
    * @param value 值
    */
    void setUserPassword(String value);


    /**
    * 获取-激活
    * 
    * @return 值
    */
    emYesNo getActivated();

    /**
    * 设置-激活
    * 
    * @param value 值
    */
    void setActivated(emYesNo value);


    /**
    * 获取-超级用户
    * 
    * @return 值
    */
    emYesNo getSupperUser();

    /**
    * 设置-超级用户
    * 
    * @param value 值
    */
    void setSupperUser(emYesNo value);


    /**
    * 获取-电子邮件地址
    * 
    * @return 值
    */
    String geteMail();

    /**
    * 设置-电子邮件地址
    * 
    * @param value 值
    */
    void seteMail(String value);


    /**
    * 获取-对象编号
    * 
    * @return 值
    */
    Integer getObjectKey();

    /**
    * 设置-对象编号
    * 
    * @param value 值
    */
    void setObjectKey(Integer value);


    /**
    * 获取-对象类型
    * 
    * @return 值
    */
    String getObjectCode();

    /**
    * 设置-对象类型
    * 
    * @param value 值
    */
    void setObjectCode(String value);


    /**
    * 获取-创建日期
    * 
    * @return 值
    */
    DateTime getCreateDate();

    /**
    * 设置-创建日期
    * 
    * @param value 值
    */
    void setCreateDate(DateTime value);


    /**
    * 获取-创建时间
    * 
    * @return 值
    */
    Short getCreateTime();

    /**
    * 设置-创建时间
    * 
    * @param value 值
    */
    void setCreateTime(Short value);


    /**
    * 获取-修改日期
    * 
    * @return 值
    */
    DateTime getUpdateDate();

    /**
    * 设置-修改日期
    * 
    * @param value 值
    */
    void setUpdateDate(DateTime value);


    /**
    * 获取-修改时间
    * 
    * @return 值
    */
    Short getUpdateTime();

    /**
    * 设置-修改时间
    * 
    * @param value 值
    */
    void setUpdateTime(Short value);


    /**
    * 获取-数据源
    * 
    * @return 值
    */
    String getDataSource();

    /**
    * 设置-数据源
    * 
    * @param value 值
    */
    void setDataSource(String value);


    /**
    * 获取-实例号（版本）
    * 
    * @return 值
    */
    Integer getLogInst();

    /**
    * 设置-实例号（版本）
    * 
    * @param value 值
    */
    void setLogInst(Integer value);


    /**
    * 获取-服务系列
    * 
    * @return 值
    */
    Integer getSeries();

    /**
    * 设置-服务系列
    * 
    * @param value 值
    */
    void setSeries(Integer value);


    /**
    * 获取-创建用户
    * 
    * @return 值
    */
    Integer getCreateUserSign();

    /**
    * 设置-创建用户
    * 
    * @param value 值
    */
    void setCreateUserSign(Integer value);


    /**
    * 获取-修改用户
    * 
    * @return 值
    */
    Integer getUpdateUserSign();

    /**
    * 设置-修改用户
    * 
    * @param value 值
    */
    void setUpdateUserSign(Integer value);


    /**
    * 获取-创建动作标识
    * 
    * @return 值
    */
    String getCreateActionId();

    /**
    * 设置-创建动作标识
    * 
    * @param value 值
    */
    void setCreateActionId(String value);


    /**
    * 获取-更新动作标识
    * 
    * @return 值
    */
    String getUpdateActionId();

    /**
    * 设置-更新动作标识
    * 
    * @param value 值
    */
    void setUpdateActionId(String value);


    /**
    * 获取-审批状态
    * 
    * @return 值
    */
    emApprovalStatus getApprovalStatus();

    /**
    * 设置-审批状态
    * 
    * @param value 值
    */
    void setApprovalStatus(emApprovalStatus value);


    /**
    * 获取-数据所有者
    * 
    * @return 值
    */
    Integer getDataOwner();

    /**
    * 设置-数据所有者
    * 
    * @param value 值
    */
    void setDataOwner(Integer value);


    /**
    * 获取-数据所属组织
    * 
    * @return 值
    */
    String getOrganization();

    /**
    * 设置-数据所属组织
    * 
    * @param value 值
    */
    void setOrganization(String value);



}
