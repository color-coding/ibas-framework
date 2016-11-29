package org.colorcoding.ibas.bobas.logics;

import java.util.HashMap;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.expressions.ExpressionFactory;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinks;
import org.colorcoding.ibas.bobas.expressions.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsChain implements IBusinessLogicsChain {

    private String id;

    @Override
    public final void setId(String value) {
        this.id = value;
    }

    @Override
    public final String getId() {
        return this.id;
    }

    private IBusinessObjectBase trigger;

    @Override
    public final IBusinessObjectBase getTrigger() {
        return this.trigger;
    }

    @Override
    public final void setTrigger(IBusinessObjectBase bo) {
        // 仅可赋值一次
        if (this.trigger == null)
            this.trigger = bo;
    }

    private IBORepository repository;

    protected final IBORepository getRepository() {
        return this.repository;
    }

    @Override
    public final void useRepository(IBORepository boRepository) {
        if (this.repository != null) {
            throw new RuntimeException(i18n.prop("msg_bobas_not_supported"));
        }
        this.repository = boRepository;
    }

    /**
     * 分析数据，获取契约
     * 
     * @param bo
     *            数据
     * @return 具有的契约
     */
    protected IBusinessLogic<?>[] analyzeContracts(IBusinessObjectBase bo) {
        ArrayList<IBusinessLogic<?>> contracts = new ArrayList<>();
        if (bo == null) {
            return contracts.toArray(new IBusinessLogic[] {});
        }
        // 先子项，再自身
        // 注意：避免嵌套后无限循环寻找契约
        if (bo instanceof IManageFields) {
            IManageFields boFields = (IManageFields) bo;
            for (IFieldData item : boFields.getFields()) {
                if (item == null || !item.isSavable() || item.getValue() == null) {
                    continue;
                }
                if (item.getValue() instanceof IBusinessObjectBase) {
                    IBusinessLogic<?>[] tmpLogics = this.analyzeContracts((IBusinessObjectBase) item.getValue());
                    // 记录父项
                    for (IBusinessLogic<?> tmpLogic : tmpLogics) {
                        if (tmpLogic instanceof BusinessLogic<?, ?>) {
                            BusinessLogic<?, ?> logic = (BusinessLogic<?, ?>) tmpLogic;
                            logic.setParent(bo);
                        }
                    }
                    contracts.addAll(tmpLogics);
                } else if (item.getValue() instanceof IBusinessObjectListBase) {
                    IBusinessObjectListBase<?> bos = (IBusinessObjectListBase<?>) item.getValue();
                    for (IBusinessObjectBase boItem : bos) {
                        IBusinessLogic<?>[] tmpLogics = this.analyzeContracts(boItem);
                        // 记录父项
                        for (IBusinessLogic<?> tmpLogic : tmpLogics) {
                            if (tmpLogic instanceof BusinessLogic<?, ?>) {
                                BusinessLogic<?, ?> logic = (BusinessLogic<?, ?>) tmpLogic;
                                logic.setParent(bo);
                            }
                        }
                        contracts.addAll(tmpLogics);
                    }
                }
            }
        }
        // 分析数据有哪些契约
        if (bo instanceof IBusinessLogicContract) {
            IBusinessLogicsManager logicsManager = BusinessLogicsFactory.createManager();
            Class<?> tmpClass = bo.getClass();
            // 开始检查契约
            while (tmpClass != null) {
                for (Class<?> item : tmpClass.getInterfaces()) {
                    boolean exists = false;
                    for (Class<?> subItem : item.getInterfaces()) {
                        if (subItem.equals(IBusinessLogicContract.class)) {
                            // 业务逻辑契约的扩展类型
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        // 存在契约，创建契约对应的逻辑实例
                        RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_LOGICS_EXISTING_CONTRACT,
                                bo.getClass().getName(), item.getName());
                        IBusinessLogic<?> logic = logicsManager.createLogic(item);
                        if (logic == null) {
                            throw new NotFoundBusinessLogicsException(item.getName());
                        }
                        if (logic instanceof BusinessLogic<?, ?>) {
                            BusinessLogic<?, ?> aLogic = (BusinessLogic<?, ?>) logic;
                            aLogic.setContract((IBusinessLogicContract) bo);
                            aLogic.setRepository(this.getRepository());
                            aLogic.setLogicsChain(this);
                        }
                        contracts.add(logic);
                    }
                }
                // 检查基类的契约
                tmpClass = tmpClass.getSuperclass();
            }
        }
        return contracts.toArray(new IBusinessLogic<?>[] {});
    }

    private HashMap<IBusinessObjectBase, IBusinessLogic<?>[]> logics;

    /**
     * 业务逻辑列表
     * 
     * @return
     */
    private final HashMap<IBusinessObjectBase, IBusinessLogic<?>[]> getLogics() {
        if (this.logics == null) {
            this.logics = new HashMap<IBusinessObjectBase, IBusinessLogic<?>[]>();
        }
        return this.logics;
    }

    /**
     * 获取业务逻辑
     * 
     * @param bo
     *            对象
     * @return
     */
    protected final IBusinessLogic<?>[] getLogics(IBusinessObjectBase bo) {
        if (this.getLogics().containsKey(bo)) {
            // 已存在bo的业务逻辑
            return this.getLogics().get(bo);
        }
        // 不存在bo的业务逻辑
        IBusinessLogic<?>[] logics = this.analyzeContracts(bo);
        this.getLogics().put(bo, logics);
        return logics;
    }

    @Override
    public final void forwardLogics(IBusinessObjectBase bo) {
        if (bo == null) {
            return;
        }
        IBusinessLogic<?>[] logics = this.getLogics(bo);
        for (IBusinessLogic<?> logic : logics) {
            logic.forward();
        }
    }

    @Override
    public final void reverseLogics(IBusinessObjectBase bo) {
        if (bo == null) {
            return;
        }
        IBusinessLogic<?>[] logics = this.getLogics(bo);
        for (IBusinessLogic<?> logic : logics) {
            logic.reverse();
        }
    }

    @Override
    public final void commit(IBusinessObjectBase bo) {
        if (bo == null) {
            return;
        }
        IBusinessLogic<?>[] logics = this.getLogics(bo);
        for (IBusinessLogic<?> logic : logics) {
            logic.commit();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> B fetchBeAffected(ICriteria criteria, Class<B> type) {
        JudgmentLinks judgmentLinks = ExpressionFactory.create().createBOJudgmentLinks(criteria.getConditions());
        for (IBusinessLogic<?>[] logics : this.getLogics().values()) {
            for (IBusinessLogic<?> logic : logics) {
                if (logic.getBeAffected() == null) {
                    continue;
                }
                boolean match = false;
                if (type.isInstance(logic.getBeAffected())) {
                    match = true;
                }
                if (match) {
                    // 类型相符
                    try {
                        if (judgmentLinks.judge(logic.getBeAffected())) {
                            return (B) logic.getBeAffected();
                        }
                    } catch (JudmentOperationException e) {
                        RuntimeLog.log(e);
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> B fetchOldParent(ICriteria criteria, Class<B> type) {
        JudgmentLinks judgmentLinks = ExpressionFactory.create().createBOJudgmentLinks(criteria.getConditions());
        for (IBusinessLogic<?>[] logics : this.getLogics().values()) {
            for (IBusinessLogic<?> item : logics) {
                if (item instanceof BusinessLogic<?, ?>) {
                    BusinessLogic<?, ?> logic = (BusinessLogic<?, ?>) item;
                    if (logic.getOldParent() == null) {
                        continue;
                    }
                    boolean match = false;
                    if (type.isInstance(logic.getOldParent())) {
                        match = true;
                    }
                    if (match) {
                        // 类型相符
                        try {
                            if (judgmentLinks.judge(logic.getOldParent())) {
                                return (B) logic.getOldParent();
                            }
                        } catch (JudmentOperationException e) {
                            RuntimeLog.log(e);
                        }
                    }
                }
            }
        }
        return null;
    }
}
