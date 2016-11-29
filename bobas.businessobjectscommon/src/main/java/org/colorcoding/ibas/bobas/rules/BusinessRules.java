package org.colorcoding.ibas.bobas.rules;

import java.util.List;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 业务规则实现
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRules implements IBusinessRules {

    public BusinessRules(Class<?> type) {
        this.setBOType(type);
    }

    private Class<?> boType;

    public final Class<?> getBOType() {
        return boType;
    }

    private final void setBOType(Class<?> boType) {
        this.boType = boType;
    }

    private volatile boolean initialized;

    @Override
    public final boolean isInitialized() {
        return this.initialized;
    }

    private final void setInitialized(boolean value) {
        this.initialized = value;
    }

    private final void setInitialized() {
        this.setInitialized(true);
    }

    private ArrayList<IBusinessRule> rules;

    protected final ArrayList<IBusinessRule> getRules() {
        if (this.rules == null) {
            synchronized (this) {
                if (this.rules == null) {
                    this.rules = new ArrayList<>();
                }
            }
        }
        return this.rules;
    }

    @Override
    public final void registerRules(IBusinessRule[] rules) {
        if (rules == null) {
            return;
        }
        this.setInitialized();
        for (IBusinessRule rule : rules) {
            if (this.checkRules(rule)) {
                this.getRules().add(rule);
            }
        }
    }

    /**
     * 检查业务规则是否注册
     * 
     * @param rule
     * @return
     */
    protected boolean checkRules(IBusinessRule rule) {
        return true;
    }

    @Override
    public final void execute(IBusinessObjectBase bo, IPropertyInfo<?>... properties) throws BusinessRuleException {
        ArrayList<IBusinessRule> doRules = new ArrayList<>();
        if (properties == null || properties.length == 0) {
            // 未指定执行属性，全部执行
            doRules.addAll(this.getRules());
        } else {
            // 指定执行属性，仅执行有此属性的规则
            for (IBusinessRule rule : this.getRules()) {
                if (rule.getInputProperties().isEmpty()) {
                    // 没有规定输入属性，则不执行
                    continue;
                }
                // 判断是否执行
                for (IPropertyInfo<?> propertyInfo : properties) {
                    if (rule.getInputProperties().contains(propertyInfo)) {
                        doRules.add(rule);
                        break;
                    }
                }
            }
        }
        this.execute(bo, doRules);
    }

    /**
     * 执行业务逻辑
     * 
     * @param bo
     *            执行逻辑的对象
     * @param rules
     *            执行的逻辑
     */
    protected void execute(IBusinessObjectBase bo, List<IBusinessRule> rules) throws BusinessRuleException {
        if (rules == null || rules.isEmpty()) {
            return;
        }
        for (IBusinessRule rule : rules) {
            rule.execute(bo);
        }
    }
}
