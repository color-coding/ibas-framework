package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.task.Daemon;
import org.colorcoding.ibas.bobas.task.IDaemonTask;

/**
 * 所有权管理员工厂
 */
public class OwnershipFactory {

	private OwnershipFactory() {
	}

	private volatile static OwnershipJudger instance;

	public synchronized static OwnershipJudger createJudger() {
		if (instance == null) {
			synchronized (OwnershipFactory.class) {
				if (instance == null) {
					instance = new Factory().create();

					// 注册清理任务
					Daemon.register(new IDaemonTask() {

						@Override
						public void run() {
							if (instance == null) {
								return;
							}
							instance = null;
						}

						@Override
						public String getName() {
							return "ownership cleanner";
						}

						private long interval = MyConfiguration.isDebugMode() ? 600 : 1800;

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

	private static class Factory extends ConfigurableFactory<OwnershipJudger> {

		public synchronized OwnershipJudger create() {
			return this.create(MyConfiguration.CONFIG_ITEM_OWNERSHIP_WAY, OwnershipJudger.class.getSimpleName());
		}

		@Override
		protected OwnershipJudger createDefault(String typeName) {
			return new OwnershipJudger() {

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
