package org.colorcoding.ibas.bobas.period;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.task.Daemon;
import org.colorcoding.ibas.bobas.task.IDaemonTask;

/**
 * 期间工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class PeriodFactory {
	private PeriodFactory() {
	}

	private volatile static PeriodsManager instance;

	public synchronized static PeriodsManager createManager() {
		if (instance == null) {
			synchronized (PeriodFactory.class) {
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
								PeriodsManager orgManager = new Factory().create();
								orgManager.initialize();
								instance = orgManager;
							} catch (Exception e) {
								// 组织刷新失败，清空
								instance = null;
								Logger.log(e);
							}
						}

						@Override
						public String getName() {
							return "Periods cleanner";
						}

						private long interval = MyConfiguration.isDebugMode() ? 300 : 3600;

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

	private static class Factory extends ConfigurableFactory<PeriodsManager> {

		public synchronized PeriodsManager create() {
			return this.create(MyConfiguration.CONFIG_ITEM_PERIODS_WAY, PeriodsManager.class.getSimpleName());
		}

		@Override
		protected PeriodsManager createDefault(String typeName) {
			return new PeriodsManager() {

				@Override
				public void initialize() {
				}

				@Override
				public void applyPeriod(IPeriodData bo) throws PeriodException {
				}

				@Override
				public void checkPeriod(IPeriodData bo) throws PeriodException {
				}
			};
		}
	}
}
