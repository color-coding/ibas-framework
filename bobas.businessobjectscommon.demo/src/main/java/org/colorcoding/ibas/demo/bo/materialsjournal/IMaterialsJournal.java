package org.colorcoding.ibas.demo.bo.materialsjournal;

import java.math.*;

import org.colorcoding.ibas.bobas.bo.*;
import org.colorcoding.ibas.bobas.data.*;

/**
* 仓库日记账 接口
* 
*/
public interface IMaterialsJournal extends IBOSimple {

    /**
    * 获取-物料编码
    * 
    * @return 值
    */
    String getItemCode();

    /**
    * 设置-物料编码
    * 
    * @param value 值
    */
    void setItemCode(String value);


    /**
    * 获取-仓库编号
    * 
    * @return 值
    */
    String getWarehouseCode();

    /**
    * 设置-仓库编号
    * 
    * @param value 值
    */
    void setWarehouseCode(String value);


    /**
    * 获取-基础单据类型
    * 
    * @return 值
    */
    String getBaseDocumentType();

    /**
    * 设置-基础单据类型
    * 
    * @param value 值
    */
    void setBaseDocumentType(String value);


    /**
    * 获取-基础单据号
    * 
    * @return 值
    */
    Integer getBaseDocumentEntry();

    /**
    * 设置-基础单据号
    * 
    * @param value 值
    */
    void setBaseDocumentEntry(Integer value);


    /**
    * 获取-基础单据行
    * 
    * @return 值
    */
    Integer getBaseDocumentLineId();

    /**
    * 设置-基础单据行
    * 
    * @param value 值
    */
    void setBaseDocumentLineId(Integer value);


    /**
    * 获取-数量
    * 
    * @return 值
    */
    BigDecimal getQuantity();

    /**
    * 设置-数量
    * 
    * @param value 值
    */
    void setQuantity(BigDecimal value);


    /**
    * 获取-编号
    * 
    * @return 值
    */
    Integer getObjectKey();

    /**
    * 设置-编号
    * 
    * @param value 值
    */
    void setObjectKey(Integer value);


    /**
    * 获取-类型
    * 
    * @return 值
    */
    String getObjectCode();

    /**
    * 设置-类型
    * 
    * @param value 值
    */
    void setObjectCode(String value);


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
    * 获取-编号系列
    * 
    * @return 值
    */
    Integer getSeries();

    /**
    * 设置-编号系列
    * 
    * @param value 值
    */
    void setSeries(Integer value);


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
    * 获取-团队成员
    * 
    * @return 值
    */
    String getTeamMembers();

    /**
    * 设置-团队成员
    * 
    * @param value 值
    */
    void setTeamMembers(String value);


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
    * 获取-已激活的
    * 
    * @return 值
    */
    emYesNo getActivated();

    /**
    * 设置-已激活的
    * 
    * @param value 值
    */
    void setActivated(emYesNo value);


    /**
    * 获取-参考1
    * 
    * @return 值
    */
    String getReference1();

    /**
    * 设置-参考1
    * 
    * @param value 值
    */
    void setReference1(String value);


    /**
    * 获取-参考2
    * 
    * @return 值
    */
    String getReference2();

    /**
    * 设置-参考2
    * 
    * @param value 值
    */
    void setReference2(String value);


    /**
    * 获取-备注
    * 
    * @return 值
    */
    String getRemarks();

    /**
    * 设置-备注
    * 
    * @param value 值
    */
    void setRemarks(String value);


    /**
    * 获取-已引用
    * 
    * @return 值
    */
    emYesNo getReferenced();

    /**
    * 设置-已引用
    * 
    * @param value 值
    */
    void setReferenced(emYesNo value);


    /**
    * 获取-删除的
    * 
    * @return 值
    */
    emYesNo getDeleted();

    /**
    * 设置-删除的
    * 
    * @param value 值
    */
    void setDeleted(emYesNo value);



}
