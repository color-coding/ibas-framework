package org.colorcoding.ibas.bobas.organization.demo;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

public class OrganizationManager extends org.colorcoding.ibas.bobas.organization.OrganizationManager {

	@Override
	public void initialize() {
		this.users = new ArrayList<>();
		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			for (org.colorcoding.ibas.demo.bo.user.IUser item : boRepository.fetchUser(new Criteria())
					.getResultObjects()) {
				this.users.add(new IUser() {

					@Override
					public String getToken() {
						return item.getUserPassword();
					}

					@Override
					public int getId() {
						return item.getObjectKey();
					}

					@Override
					public String getBelong() {
						return item.getOrganization();
					}

				});

			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	private volatile ArrayList<IUser> users;

	@Override
	public IUser getUser(String token) {
		if (Strings.equals(SYSTEM_USER.getToken(), token)) {
			return SYSTEM_USER;
		}
		if (this.users != null) {
			for (IUser user : this.users) {
				if (Strings.equals(user.getToken(), token)) {
					return user;
				}
			}
		}
		return OrganizationFactory.UNKNOWN_USER;
	}

	@Override
	public IUser getUser(int id) {
		if (Numbers.equals(SYSTEM_USER.getId(), id)) {
			return SYSTEM_USER;
		}
		if (this.users != null) {
			for (IUser user : this.users) {
				if (Numbers.equals(user.getId(), id)) {
					return user;
				}
			}
		}
		return OrganizationFactory.UNKNOWN_USER;
	}

	@Override
	public IUser register(IUser user) {
		if (this.users != null) {
			this.users.add(user);
			return user;
		}
		return null;
	}

}
