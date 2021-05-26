package org.colorcoding.ibas.bobas.rule;

import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IManagedProperties;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.TrackableBase;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 普通业务规则
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class BusinessRuleCommon extends BusinessRule {

	private boolean affectedInSilent;

	public final boolean isAffectedInSilent() {
		return affectedInSilent;
	}

	public final void setAffectedInSilent(boolean affectedInSilent) {
		this.affectedInSilent = affectedInSilent;
	}

	@Override
	public final void execute(IBusinessObject bo, String trigger) throws BusinessRuleException {
		try {
			BusinessRuleContext context = new BusinessRuleContext();
			context.setSource(bo);
			context.setTrigger(trigger);
			// 赋值输入属性
			if (bo instanceof IManagedProperties) {
				IManagedProperties boProperties = (IManagedProperties) bo;
				for (IPropertyInfo<?> propertyInfo : this.getInputProperties()) {
					Object value = boProperties.getProperty(propertyInfo);
					context.getInputValues().put(propertyInfo, value);
				}
			}
			// 执行规则
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, MSG_RULES_EXECUTING, this.getClass().getName(), this.getName());
			}
			this.execute(context);
			// 赋值输出属性
			if (bo instanceof IManagedProperties) {
				TrackableBase trackable = null;
				IManagedProperties boProperties = (IManagedProperties) bo;
				if (this.isAffectedInSilent() && !bo.isLoading() && bo instanceof TrackableBase) {
					// 静默模式，不触发属性改变事件
					trackable = (TrackableBase) bo;
					trackable.setLoading(true);
				}
				for (IPropertyInfo<?> propertyInfo : this.getAffectedProperties()) {
					@SuppressWarnings("unchecked")
					IPropertyInfo<Object> property = (IPropertyInfo<Object>) propertyInfo;
					Object value = context.getOutputValues().get(propertyInfo);
					if (value != null) {
						boProperties.setProperty(property, value);
					}
				}
				if (trackable != null) {
					// 取消静默模式
					trackable.setLoading(false);
				}
			}
		} catch (Exception e) {
			throw new BusinessRuleException(I18N.prop("msg_bobas_bo_executing_business_rule_faild", bo, this.getName()),
					e);
		}
	}

	/**
	 * 执行业务逻辑
	 * 
	 * @param context 内容
	 */
	protected abstract void execute(BusinessRuleContext context) throws Exception;

	/**
	 * 业务规则执行内容
	 * 
	 * @author Niuren.Zhu
	 *
	 */
	protected class BusinessRuleContext {

		public BusinessRuleContext() {

		}

		public BusinessRuleContext(IBusinessObject source) {
			this();
			this.setSource(source);
		}

		private String trigger;

		public final String getTrigger() {
			return trigger;
		}

		public final void setTrigger(String trigger) {
			this.trigger = trigger;
		}

		private IBusinessObject source;

		public final IBusinessObject getSource() {
			return source;
		}

		final void setSource(IBusinessObject source) {
			this.source = source;
		}

		private Map<IPropertyInfo<?>, Object> inputValues;

		public final Map<IPropertyInfo<?>, Object> getInputValues() {
			if (this.inputValues == null) {
				this.inputValues = new HashMap<>(3);
			}
			return this.inputValues;
		}

		private Map<IPropertyInfo<?>, Object> outputValues;

		public final Map<IPropertyInfo<?>, Object> getOutputValues() {
			if (this.outputValues == null) {
				this.outputValues = new HashMap<>(1);
			}
			return this.outputValues;
		}

	}
}
