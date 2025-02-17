package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.task.Daemon;
import org.colorcoding.ibas.bobas.task.IDaemonTask;
import org.colorcoding.ibas.bobas.task.InvalidDaemonTaskException;

/**
 * 所有权管理员工厂
 */
public class OwnershipFactory {

	private OwnershipFactory() {
	}

	private volatile static IOwnershipJudger instance;

	public synchronized static IOwnershipJudger createJudger() {
		if (instance == null) {
			synchronized (OwnershipFactory.class) {
				if (instance == null) {
					instance = new _OwnershipFactory().createJudger();
					try {
						Daemon.register(new IDaemonTask() {

							@Override
							public void run() {
								if (instance == null) {
									// 未初始化，不做处理
									return;
								}
								instance = null;
							}

							@Override
							public String getName() {
								return "ownership cleanner";
							}

							private long interval = MyConfiguration.getConfigValue(
									MyConfiguration.CONFIG_ITEM_OWNERSHIP_JUDGER_EXPIRY_VALUE,
									MyConfiguration.isDebugMode() ? 60 : 300);

							@Override
							public long getInterval() {
								return this.interval;
							}
						});
					} catch (InvalidDaemonTaskException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance;
	}

	private static class _OwnershipFactory extends ConfigurableFactory<IOwnershipJudger> {

		public synchronized IOwnershipJudger createJudger() {
			return this.create(MyConfiguration.CONFIG_ITEM_OWNERSHIP_WAY, "OwnershipJudger");
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

				@Override
				public ICriteria filterCriteria(BusinessObjectUnit boUnit, IUser user) {
					return null;
				}

			};
		}
	}
}
