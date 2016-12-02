package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.approval.ApprovalException;
import org.colorcoding.ibas.bobas.common.IOperationInformation;
import org.colorcoding.ibas.bobas.common.OperationInformation;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 所有权管理员工厂
 */
public class OwnershipFactory extends ConfigurableFactory {

    private OwnershipFactory() {

    }

    /**
     * 操作信息：数据检索数量
     */
    public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_FETCH_COUNT = "DATA_OWNERSHIP_FETCH_COUNT";
    /**
     * 操作信息：数据过滤数量
     */
    public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT = "DATA_OWNERSHIP_FILTER_COUNT";
    /**
     * 操作信息标签：权限判断
     */
    public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_TAG = "DATA_OWNERSHIP_JUDGE";

    public static IOperationInformation[] createOwnershipJudgeInfo(Integer fetchCount, Integer filterCount) {
        OperationInformation oifetch = new OperationInformation();
        oifetch.setName(OPERATION_INFORMATION_DATA_OWNERSHIP_FETCH_COUNT);
        oifetch.setTag(OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
        oifetch.setContents(i18n.prop("msg_bobas_data_ownership_fetch_count", fetchCount));
        OperationInformation oiFilter = new OperationInformation();
        oiFilter.setName(OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT);
        oiFilter.setTag(OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
        oiFilter.setContents(i18n.prop("msg_bobas_data_ownership_filter_count", filterCount));
        return new IOperationInformation[] { oifetch, oiFilter };
    }

    private volatile static IOwnershipJudger ownershipJudger = null;

    /**
     * 创建流程管理员实例
     * 
     * @return
     * @throws ApprovalException
     */
    public synchronized static IOwnershipJudger createJudger() throws RuntimeException {
        if (ownershipJudger == null) {
            synchronized (OwnershipFactory.class) {
                if (ownershipJudger == null) {
                    // 尝试初始化
                    try {
                        ownershipJudger = newJudger(MyConfiguration
                                .getConfigValue(MyConfiguration.CONFIG_ITEM_OWNERSHIP_WAY, "").toLowerCase());
                    } catch (Exception e) {
                        throw new RuntimeException(i18n.prop("msg_bobas_create_data_ownership_judger_falid"), e);
                    }
                    if (ownershipJudger == null) {
                        // 初始化后仍然无效
                        throw new RuntimeException(i18n.prop("msg_bobas_create_data_ownership_judger_falid"));
                    }
                }
            }
        }
        return ownershipJudger;

    }

    private static IOwnershipJudger newJudger(String type)
            throws BOFactoryException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (type == null || type.isEmpty()) {
            // 没有指定实现方式，则使用代理
            return new IOwnershipJudger() {

                @Override
                public boolean canRead(IDataOwnership bo, IUser user) {
                    return true;
                }

                @Override
                public boolean canRead(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
                    return this.canRead(bo, user);
                }

                @Override
                public boolean canSave(IDataOwnership bo, IUser user) {
                    return true;
                }

                @Override
                public boolean canSave(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
                    return this.canSave(bo, user);
                }

                @Override
                public boolean canCall(String className, String methodName, IUser user)
                        throws NotConfiguredException, UnauthorizedException {
                    return true;
                }

            };
        }
        Class<?> managerClass = getInstance(OwnershipFactory.class, type, "OwnershipJudger");
        if (managerClass == null) {
            throw new ClassNotFoundException(i18n.prop("msg_bobas_not_found_data_ownership_judger"));
        }
        IOwnershipJudger judger = (IOwnershipJudger) managerClass.newInstance();
        if (judger == null) {
            throw new NullPointerException(i18n.prop("msg_bobas_not_found_data_ownership_judger"));
        }
        RuntimeLog.log(RuntimeLog.MSG_OWNERSHIP_JUDGER_CREATED, managerClass.getName());
        return judger;
    }
}
