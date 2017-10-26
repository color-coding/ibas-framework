package org.colorcoding.ibas.bobas.organization;

import java.util.UUID;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.data.ArrayList;

/**
 * 审批工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class OrganizationFactory extends ConfigurableFactory<IOrganizationManager> {
	private OrganizationFactory() {
	}

	private volatile static OrganizationFactory instance;

	public synchronized static OrganizationFactory create() {
		if (instance == null) {
			synchronized (OrganizationFactory.class) {
				if (instance == null) {
					instance = new OrganizationFactory();
				}
			}
		}
		return instance;
	}

	@Override
	protected IOrganizationManager createDefault(String typeName) {
		return new IOrganizationManager() {
			@Override
			public IUser getUser(String token) {
				if (token == null) {
					return UNKNOWN_USER;
				}
				if (token.equals(SYSTEM_USER.getToken())) {
					return SYSTEM_USER;
				}
				for (IUser item : this.users) {
					if (token.equals(item.getToken())) {
						return item;
					}
				}
				return UNKNOWN_USER;
			}

			@Override
			public IUser getUser(int id) {
				if (id == UNKNOWN_USER.getId()) {
					return UNKNOWN_USER;
				}
				if (id == SYSTEM_USER.getId()) {
					return SYSTEM_USER;
				}
				for (IUser item : this.users) {
					if (id == item.getId()) {
						return item;
					}
				}
				return UNKNOWN_USER;
			}

			@Override
			public void initialize() {
			}

			@Override
			public String[] getRoles(IUser user) {
				for (IUser item : this.users) {
					if (item == user) {
						return new String[] { item.getBelong() };
					}
				}
				return new String[] {};
			}

			private ArrayList<IUser> users = new ArrayList<>();

			@Override
			public void register(IUser user) {
				if (user == null) {
					return;
				}
				for (IUser item : this.users) {
					if (user == item) {
						return;
					}
				}
				this.users.add(user);
			}
		};
	}

	private volatile static IOrganizationManager defaultManager = null;

	public synchronized IOrganizationManager createManager() {
		if (defaultManager == null) {
			defaultManager = this.create(MyConfiguration.CONFIG_ITEM_ORGANIZATION_WAY, "OrganizationManager");
			// 有效数据，进行初始化
			defaultManager.initialize();
		}
		return defaultManager;

	}

	public static IUser UNKNOWN_USER = new IUser() {

		@Override
		public int getId() {
			return -1;
		}

		@Override
		public String getBelong() {
			return null;
		}

		@Override
		public String getToken() {
			return null;
		}

		@Override
		public void checkAuthorization(String token) throws InvalidAuthorizationException {
			throw new InvalidAuthorizationException();
		}

		@Override
		public String toString() {
			return "{UNKNOWN_USER}";
		}

	};

	public static IUser SYSTEM_USER = new IUser() {

		@Override
		public int getId() {
			return -9;
		}

		@Override
		public String getBelong() {
			return null;
		}

		private String token = UUID.randomUUID().toString();

		@Override
		public String getToken() {
			return this.token;
		}

		@Override
		public void checkAuthorization(String token) throws InvalidAuthorizationException {
			if (!this.getToken().equals(token)) {
				throw new InvalidAuthorizationException();
			}
		}

		@Override
		public String toString() {
			return "{SYSTEM_USER}";
		}

	};

}
