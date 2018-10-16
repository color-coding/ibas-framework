package org.colorcoding.ibas.bobas.period;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.IDaemonTask;

/**
 * 期间工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class PeriodsFactory extends ConfigurableFactory<IPeriodsManager> {
	private PeriodsFactory() {
	}

	private volatile static PeriodsFactory instance;

	public synchronized static PeriodsFactory create() {
		if (instance == null) {
			synchronized (PeriodsFactory.class) {
				if (instance == null) {
					instance = new PeriodsFactory();
					// 注册释放任务
					instance.register(new IDaemonTask() {

						@Override
						public void run() {
							if (instance.periodManager == null) {
								// 未初始化，不做处理
								return;
							}
							instance.periodManager = null;
						}

						@Override
						public boolean isActivated() {
							if (this.getInterval() <= 0) {
								return false;
							}
							return true;
						}

						private String name = "periods cleanner";

						@Override
						public String getName() {
							return this.name;
						}

						private long interval = MyConfiguration.getConfigValue(
								MyConfiguration.CONFIG_ITEM_PERIOD_MANAGER_EXPIRY_VALUE,
								MyConfiguration.isDebugMode() ? 600 : 1800);

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
	protected IPeriodsManager createDefault(String typeName) {
		return new IPeriodsManager() {

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

	private IPeriodsManager periodManager = null;

	public synchronized IPeriodsManager createManager() {
		if (this.periodManager == null) {
			this.periodManager = this.create(MyConfiguration.CONFIG_ITEM_PERIODS_WAY, "PeriodsManager");
			// 有效数据，进行初始化
			this.periodManager.initialize();
		}
		return this.periodManager;
	}

}
