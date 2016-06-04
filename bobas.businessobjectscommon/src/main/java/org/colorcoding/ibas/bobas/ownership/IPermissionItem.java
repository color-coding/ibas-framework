package org.colorcoding.ibas.bobas.ownership;

public interface IPermissionItem {
	String getGroup();

	void setGroup(String group);

	String getName();

	void setName(String name);

	PermissionValue getValue();

	void setValue(PermissionValue value);
}
