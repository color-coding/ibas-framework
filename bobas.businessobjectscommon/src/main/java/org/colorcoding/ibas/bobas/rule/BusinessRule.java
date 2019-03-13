package org.colorcoding.ibas.bobas.rule;

import java.util.List;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;

/**
 * 业务规则抽象
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class BusinessRule implements IBusinessRule {

	protected static final String MSG_RULES_EXECUTING = "rules: executing rule [%s - %s].";

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

	@Override
	public String toString() {
		return String.format("{business rule: %s}", this.getName());
	}
}
