package org.colorcoding.ibas.bobas.ownership;

public class PermissionItem implements IPermissionItem {

	public PermissionItem() {

	}

	public PermissionItem(String group, String name, PermissionValue value) {
		this.setGroup(group);
		this.setName(name);
		this.setValue(value);
	}

	private String group;

	@Override
	public final String getGroup() {
		return group;
	}

	@Override
	public final void setGroup(String group) {
		this.group = group;
	}

	private String name;

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void setName(String name) {
		this.name = name;
	}

	private PermissionValue value;

	@Override
	public final PermissionValue getValue() {
		return value;
	}

	@Override
	public final void setValue(PermissionValue value) {
		this.value = value;
	}

}
