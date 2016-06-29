package org.colorcoding.ibas.bobas.organization;

/**
 * 组织管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class OrganizationManager implements IOrganizationManager {

	public OrganizationManager() {

	}

	@Override
	public IUser getUser(String token) {
		return new UnknownUser();
	}

	@Override
	public IUser getUser(int id) {
		return new UnknownUser();
	}

}
