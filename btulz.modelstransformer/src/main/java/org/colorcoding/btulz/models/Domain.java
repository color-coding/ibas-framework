package org.colorcoding.btulz.models;

public class Domain implements IDomain {

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

	private IModels models;

	@Override
	public IModels getModels() {
		if (this.models == null) {
			this.models = new Models();
		}
		return this.models;
	}

	@Override
	public String toString() {
		return String.format("domain:%s", this.getName());
	}
}
