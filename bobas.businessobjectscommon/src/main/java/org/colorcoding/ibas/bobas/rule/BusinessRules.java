package org.colorcoding.ibas.bobas.rule;

import java.util.Collection;
import java.util.Iterator;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 业务规则集合
 * 
 * @author Niuren.Zhu
 *
 */
class BusinessRules implements IBusinessRules {

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

	private Collection<IBusinessRule> rules;

	protected final Collection<IBusinessRule> getRules() {
		if (this.rules == null) {
			this.rules = new ArrayList<>();
		}
		return this.rules;
	}

	@Override
	public final Iterator<IBusinessRule> iterator() {
		return this.getRules().iterator();
	}

	@Override
	public final int size() {
		return this.getRules().size();
	}

	@Override
	public final void register(IBusinessRule[] rules) {
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
	public final void execute(IBusinessObject bo, IPropertyInfo<?>... properties) throws BusinessRuleException {
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
		if (!doRules.isEmpty()) {
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, "rules: start executing %s rules.", bo);
			}
			StringBuilder builder = new StringBuilder();
			for (IPropertyInfo<?> propertyInfo : properties) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(propertyInfo.getName());
			}
			String trigger = builder.toString();
			for (IBusinessRule rule : doRules) {
				rule.execute(bo, trigger);
			}
		}
	}

}
