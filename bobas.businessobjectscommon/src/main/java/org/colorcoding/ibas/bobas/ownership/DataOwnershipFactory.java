package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.common.IOperationInformation;
import org.colorcoding.ibas.bobas.common.OperationInformation;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.i18n.i18n;

/**
 * 所有权管理员工厂
 */
public class DataOwnershipFactory extends ConfigurableFactory {
	private DataOwnershipFactory() {

	}

	private static IOwnershipJudge instance = null;

	public static IOwnershipJudge createJudge() {
		if (instance == null) {
			// 加载自定义工厂

		}
		return instance;
	}

	public static String OPERATION_INFORMATION_DATA_OWNERSHIP_fetch_COUNT = "DATA_OWNERSHIP_FETCH_COUNT";
	public static String OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT = "DATA_OWNERSHIP_FILTER_COUNT";
	public static String OPERATION_INFORMATION_DATA_OWNERSHIP_TAG = "DATA_OWNERSHIP_JUDGE";

	public static IOperationInformation[] createOwnershipJudgeInfo(Integer fetchCount, Integer filterCount) {
		OperationInformation oifetch = new OperationInformation();
		oifetch.setName(OPERATION_INFORMATION_DATA_OWNERSHIP_fetch_COUNT);
		oifetch.setTag(OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
		oifetch.setContents(i18n.prop("msg_bobas_data_ownership_fetch_count", fetchCount));
		OperationInformation oiFilter = new OperationInformation();
		oiFilter.setName(OPERATION_INFORMATION_DATA_OWNERSHIP_FILTER_COUNT);
		oiFilter.setTag(OPERATION_INFORMATION_DATA_OWNERSHIP_TAG);
		oiFilter.setContents(i18n.prop("msg_bobas_data_ownership_filter_count", filterCount));
		return new IOperationInformation[] { oifetch, oiFilter };
	}
}
