package org.colorcoding.ibas.bobas.organization;

import java.util.UUID;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.IDaemonTask;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

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
					instance = new OrganizationFactory();// 注册释放任务
					instance.register(new IDaemonTask() {

						@Override
						public void run() {
							synchronized (instance) {
								instance.organizationManager = null;
							}
						}

						private boolean activated = true;

						@Override
						public boolean isActivated() {
							if (this.getInterval() <= 0) {
								return false;
							}
							return this.activated;
						}

						private String name = "organization cleanner";

						@Override
						public String getName() {
							return this.name;
						}

						private long interval = MyConfiguration.getConfigValue(
								MyConfiguration.CONFIG_ITEM_ORGANIZATION_MANAGER_EXPIRY_VALUE,
								MyConfiguration.isDebugMode() ? 60 : 600);

						@Override
						public long getInterval() {
							return this.interval;
						}
					});
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
				if (token != null) {
					if (token.equals(SYSTEM_USER.getToken())) {
						return SYSTEM_USER;
					}
					for (IUser item : this.getUsers()) {
						if (token.equals(item.getToken())) {
							return item;
						}
					}
				}
				return UNKNOWN_USER;
			}

			@Override
			public IUser getUser(int id) {
				if (id == SYSTEM_USER.getId()) {
					return SYSTEM_USER;
				}
				for (IUser item : this.getUsers()) {
					if (id == item.getId()) {
						return item;
					}
				}
				return UNKNOWN_USER;
			}

			@Override
			public void initialize() {
				this.users = new ArrayList<>();
			}

			@Override
			public String[] getRoles(IUser user) {
				for (IUser item : this.getUsers()) {
					if (item == user) {
						return new String[] { item.getBelong() };
					}
				}
				return new String[] {};
			}

			private volatile ArrayList<IUser> users;

			public List<IUser> getUsers() {
				if (this.users == null) {
					synchronized (this) {
						if (this.users == null) {
							this.users = new ArrayList<>();
						}
					}
				}
				return this.users;
			}

			@Override
			public void register(IUser user) {
				if (user == null) {
					return;
				}
				for (int i = 0; i < this.getUsers().size(); i++) {
					IUser item = this.getUsers().get(i);
					if (item == null) {
						continue;
					}
					if (item.getId() == user.getId()) {
						this.getUsers().set(i, user);
						return;
					}
				}
				this.getUsers().add(user);
			}
		};
	}

	private IOrganizationManager organizationManager = null;

	public synchronized IOrganizationManager createManager() {
		if (this.organizationManager == null) {
			this.organizationManager = this.create(MyConfiguration.CONFIG_ITEM_ORGANIZATION_WAY, "OrganizationManager");
			// 有效数据，进行初始化
			this.organizationManager.initialize();
		}
		return this.organizationManager;

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
