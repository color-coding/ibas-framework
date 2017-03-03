package org.colorcoding.ibas.bobas.organization;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

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
				return UNKNOWN_USER;
			}

			@Override
			public IUser getUser(int id) {
				return UNKNOWN_USER;
			}

			@Override
			public void initialize() {
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

	public final static IUser UNKNOWN_USER = new IUser() {

		@Override
		public int getId() {
			return IOrganizationManager.UNKNOWN_USER_SIGN;
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
		}

		@Override
		public String toString() {
			return String.format("{user: %s}", this.getId());
		}

	};

}
