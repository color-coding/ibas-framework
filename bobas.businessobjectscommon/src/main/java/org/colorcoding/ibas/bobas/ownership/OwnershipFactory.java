package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.IOperationInformation;
import org.colorcoding.ibas.bobas.common.OperationInformation;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.i18n.i18n;
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
				}
			}
		}
		return instance;
	}

	/**
	 * 操作信息：数据检索数量
	 */
	public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_FETCH_COUNT = "DATA_OWNERSHIP_FETCH_COUNT";
	/**
	 * 操作信息：数据过滤数量
	 */
	public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT = "DATA_OWNERSHIP_FILTER_COUNT";
	/**
	 * 操作信息标签：权限判断
	 */
	public final static String OPERATION_INFORMATION_DATA_OWNERSHIP_TAG = "DATA_OWNERSHIP_JUDGE";

	public static IOperationInformation[] createOwnershipJudgeInfo(Integer fetchCount, Integer filterCount) {
		OperationInformation oifetch = new OperationInformation();
		oifetch.setName(OPERATION_INFORMATION_DATA_OWNERSHIP_FETCH_COUNT);
		oifetch.setTag(OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
		oifetch.setContents(i18n.prop("msg_bobas_data_ownership_fetch_count", fetchCount));
		OperationInformation oiFilter = new OperationInformation();
		oiFilter.setName(OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT);
		oiFilter.setTag(OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
		oiFilter.setContents(i18n.prop("msg_bobas_data_ownership_filter_count", filterCount));
		return new IOperationInformation[] { oifetch, oiFilter };
	}

	private volatile static IOwnershipJudger ownershipJudger = null;

	public synchronized IOwnershipJudger createJudger() {
		if (ownershipJudger == null) {
			ownershipJudger = this.create(MyConfiguration.CONFIG_ITEM_OWNERSHIP_WAY, "OwnershipJudger");
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
			public boolean canRead(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
				return this.canRead(bo, user);
			}

			@Override
			public boolean canSave(IDataOwnership bo, IUser user) {
				return true;
			}

			@Override
			public boolean canSave(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException {
				return this.canSave(bo, user);
			}

			@Override
			public boolean canCall(String className, String methodName, IUser user)
					throws NotConfiguredException, UnauthorizedException {
				return true;
			}

		};
	}

}
