package org.colorcoding.ibas.bobas.rule;

import java.util.List;

import org.colorcoding.ibas.bobas.bo.BOIdentifierBuilder;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 业务规则抽象
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class BusinessRule implements IBusinessRule {

	private String name;

	public final String getName() {
		if (this.name == null || this.name.isEmpty()) {
			this.name = this.getClass().getSimpleName();
		}
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	private ArrayList<IPropertyInfo<?>> inputProperties;

	@Override
	public final List<IPropertyInfo<?>> getInputProperties() {
		if (this.inputProperties == null) {
			this.inputProperties = new ArrayList<>();
		}
		return this.inputProperties;
	}

	private ArrayList<IPropertyInfo<?>> affectedProperties;

	@Override
	public final List<IPropertyInfo<?>> getAffectedProperties() {
		if (this.affectedProperties == null) {
			this.affectedProperties = new ArrayList<>();
		}
		return this.affectedProperties;
	}

	@Override
	public final void execute(IBusinessObject bo) throws BusinessRuleException {
		this.execute(bo, null);
	}

	/**
	 * 使用对象类型描述和校验内容创建业务规则异常。
	 *
	 * @param bo         校验的业务对象
	 * @param messageKey 校验消息的语言资源键
	 * @param args       校验消息参数
	 * @return 包含对象类型引用的校验异常
	 */
	protected final BusinessRuleException createException(IBusinessObject bo, String messageKey, Object... args) {
		return new BusinessRuleException(I18N.prop("msg_bobas_business_rule_error",
				BOIdentifierBuilder.objectReference(bo), I18N.prop(messageKey, args)));
	}

	@Override
	public String toString() {
		return String.format("{bizRule: %s}", this.getName());
	}
}
