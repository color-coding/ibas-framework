package org.colorcoding.ibas.bobas.organization;

import java.util.UUID;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.task.Daemon;
import org.colorcoding.ibas.bobas.task.IDaemonTask;

/**
 * 组织工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class OrganizationFactory {

	public static IUser UNKNOWN_USER = new IUser() {

		@Override
		public int getId() {
			return -1;
		}

		@Override
		public String getBelong() {
			return Strings.VALUE_EMPTY;
		}

		@Override
		public String getToken() {
			return Strings.VALUE_EMPTY;
		}

		@Override
		public void checkAuthorization(String token) throws InvalidAuthorizationException {
			throw new InvalidAuthorizationException();
		}

		@Override
		public String toString() {
			return "{user: UNKNOWN_USER}";
		}

	};

	public static IUser SYSTEM_USER = new IUser() {

		@Override
		public int getId() {
			return -9;
		}

		@Override
		public String getBelong() {
			return Strings.VALUE_EMPTY;
		}

		private String token = UUID.randomUUID().toString();

		@Override
		public String getToken() {
			return this.token;
		}

		@Override
		public String toString() {
			return "{user: SYSTEM_USER}";
		}

	};

	private OrganizationFactory() {
	}

	private volatile static OrganizationManager instance;

	public synchronized static OrganizationManager createManager() {
		if (instance == null) {
			synchronized (OrganizationFactory.class) {
				if (instance == null) {
					instance = new Factory().create();
					instance.initialize();
					// 注册清理任务
					Daemon.register(new IDaemonTask() {
						@Override
						public void run() {
							if (instance == null) {
								// 未初始化，不做处理
								return;
							}
							// 刷新组织
							try {
								OrganizationManager manager = new Factory().create();
								manager.initialize();
								instance = manager;
							} catch (Exception e) {
								// 组织刷新失败，清空
								instance = null;
								Logger.log(e);
							}
						}

						@Override
						public String getName() {
							return "organization cleanner";
						}

						private long interval = MyConfiguration.getConfigValue(
								MyConfiguration.CONFIG_ITEM_ORGANIZATION_MANAGER_EXPIRY_VALUE,
								MyConfiguration.isDebugMode() ? 300 : 3600);

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

	private static class Factory extends ConfigurableFactory<OrganizationManager> {

		public synchronized OrganizationManager create() {
			return this.create(MyConfiguration.CONFIG_ITEM_ORGANIZATION_WAY, OrganizationManager.class.getSimpleName());
		}

		@Override
		protected OrganizationManager createDefault(String typeName) {
			return new OrganizationManager() {
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
				public IUser register(IUser user) {
					if (user != null) {
						for (int i = 0; i < this.getUsers().size(); i++) {
							IUser item = this.getUsers().get(i);
							if (item == null) {
								continue;
							}
							if (item.getId() == user.getId()) {
								this.getUsers().set(i, user);
							}
						}
						this.getUsers().add(user);
					}
					return user;
				}
			};
		}

	}
}
