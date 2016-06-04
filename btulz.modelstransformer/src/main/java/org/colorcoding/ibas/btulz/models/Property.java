package org.colorcoding.ibas.btulz.models;

import org.colorcoding.ibas.btulz.data.emPropertyType;

public abstract class Property implements IProperty {
	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private String description;

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public abstract emPropertyType getPropertyType();

	private String declaredType;

	@Override
	public String getDeclaredType() {
		return this.declaredType;
	}

	@Override
	public void setDeclaredType(String declaredType) {
		this.declaredType = declaredType;
	}

	@Override
	public String toString() {
		return String.format("property:%s type:%s", this.getName(), this.getPropertyType());
	}
}
