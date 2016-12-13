package org.colorcoding.ibas.bobas.core;

import java.util.UUID;

import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.ownership.IDataOwnership;

public abstract class BORepositoryBase implements IBORepositoryReadonly {

    public BORepositoryBase() {
        this.initialize();
    }

    protected void initialize() {

    }

    private IBOFactory boFactory = null;

    @Override
    public final IBOFactory getBOFactory() {
        if (this.boFactory == null) {
            this.boFactory = BOFactory.create();
        }
        return this.boFactory;
    }

    @Override
    public final void setBOFactory(IBOFactory factory) {
        this.boFactory = factory;
    }

    private IUser currentUser = null;

    @Override
    public final IUser getCurrentUser() {
        if (this.currentUser == null) {
            // 未设置用户则为未知用户
            this.currentUser = OrganizationFactory.UNKNOWN_USER;
        }
        return this.currentUser;
    }

    @Override
    public final void setCurrentUser(IUser user) {
        this.currentUser = user;
    }

    private String transactionId = null;

    public final String getTransactionId() {
        return transactionId;
    }

    public final void setTransactionId(String value) {
        this.transactionId = value;
    }

    public final void setTransactionId() {
        this.transactionId = UUID.randomUUID().toString();
    }

    /**
     * 标记存储信息
     * 
     * @param bo
     *            业务对象
     */
    protected void tagStorage(IBusinessObjectBase bo) {
        if (bo == null) {
            return;
        }
        if (!bo.isDirty()) {
            return;
        }
        if (bo instanceof IBOStorageTag) {
            this.tagStorage((IBOStorageTag) bo, bo.isNew());
        }
    }

    /**
     * 标记存储信息
     * 
     * @param bo
     *            业务对象
     */
    private void tagStorage(IBOStorageTag bo, boolean isNew) {
        if (isNew) {
            // 新建对象
            bo.setCreateDate(DateTime.getToday());
            bo.setCreateTime(Short.valueOf(DateTime.getNow().toString("HHmm")));
            bo.setCreateUserSign(this.getCurrentUser().getId());
            bo.setCreateActionId(this.getTransactionId());
            bo.setLogInst(1);
            if (bo instanceof IDataOwnership) {
                // 数据所有者标记
                IDataOwnership data = (IDataOwnership) bo;
                if (data.getDataOwner() == null || data.getDataOwner() == 0)
                    data.setDataOwner(this.getCurrentUser().getId());
                if (data.getOrganization() == null || data.getOrganization().isEmpty())
                    data.setOrganization(this.getCurrentUser().getBelong());
            }
        } else {
            // 更新对象
            bo.setUpdateDate(DateTime.getToday());
            bo.setUpdateTime(Short.valueOf(DateTime.getNow().toString("HHmm")));
            bo.setUpdateUserSign(this.getCurrentUser().getId());
            bo.setUpdateActionId(this.getTransactionId());
            bo.setLogInst(bo.getLogInst() + 1);
        }
    }

}
