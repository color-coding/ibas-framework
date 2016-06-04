package org.colorcoding.ibas.btulz.models;

import org.colorcoding.ibas.btulz.data.emModelType;

public class Model implements IModel {

	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private String shortName;

	@Override
	public String getShortName() {
		return this.shortName;
	}

	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
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

	private emModelType modelType;

	@Override
	public emModelType getModelType() {
		if (this.modelType == null) {
			this.modelType = emModelType.mt_Unspecified;
		}
		return this.modelType;
	}

	@Override
	public void setModelType(emModelType modelType) {
		this.modelType = modelType;
	}

	private IProperties properties;

	@Override
	public IProperties getProperties() {
		if (this.properties == null) {
			this.properties = new Properties();
		}
		return this.properties;
	}

	private String mapped;

	@Override
	public String getMapped() {
		return this.mapped;
	}

	@Override
	public void setMapped(String mapped) {
		this.mapped = mapped;
	}

	@Override
	public String toString() {
		return String.format("model:%s type:%s", this.getName(), this.getModelType());
	}
}
