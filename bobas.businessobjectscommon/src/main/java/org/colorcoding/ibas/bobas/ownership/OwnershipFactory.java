package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.IDaemonTask;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 所有权管理员工厂
 */
public class OwnershipFactory extends ConfigurableFactory<IOwnershipJudger> {

	private OwnershipFactory() {
	}

	private volatile static OwnershipFactory instance;

	public synchronized static OwnershipFactory create() {
		if (instance == null) {
			synchronized (OwnershipFactory.class) {
				if (instance == null) {
					instance = new OwnershipFactory();
					// 注册释放任务
					instance.register(new IDaemonTask() {

						@Override
						public void run() {
							synchronized (instance) {
								instance.ownershipJudger = null;
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

						private String name = "ownership cleanner";

						@Override
						public String getName() {
							return this.name;
						}

						private long interval = MyConfiguration.getConfigValue(
								MyConfiguration.CONFIG_ITEM_OWNERSHIP_JUDGER_EXPIRY_VALUE,
								MyConfiguration.isDebugMode() ? 30 : 120);

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

	private IOwnershipJudger ownershipJudger = null;

	public synchronized IOwnershipJudger createJudger() {
		if (this.ownershipJudger == null) {
			this.ownershipJudger = this.create(MyConfiguration.CONFIG_ITEM_OWNERSHIP_WAY, "OwnershipJudger");
		}
		return ownershipJudger;
	}

	@Override
	protected IOwnershipJudger createDefault(String typeName) {
		return new IOwnershipJudger() {

			@Override
			public boolean canRead(IDataOwnership bo, IUser user) {
				return true;
			}

			@Override
			public boolean canSave(IDataOwnership bo, IUser user) {
				return true;
			}

			@Override
			public boolean canRead(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
				return this.canRead(bo, user);
			}

			@Override
			public boolean canSave(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
				return this.canSave(bo, user);
			}

		};
	}

}
