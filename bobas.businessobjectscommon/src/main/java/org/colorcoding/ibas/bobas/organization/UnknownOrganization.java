package org.colorcoding.ibas.bobas.organization;

/**
 * 未知的组织
 */
public class UnknownOrganization implements IOrganization {

	public UnknownOrganization() {

	}

	/**
	 * 仅返回未知用户
	 */
	public IUser getUser(String token) {
		return new UnknownUser();
	}

	@Override
	public String toString() {
		return String.format("{Organization id = %s}", this.hashCode());
	}
}
