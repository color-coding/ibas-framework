package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.logic.IBusinessLogicContract;
import org.colorcoding.ibas.bobas.period.IPeriodData;

/**
 * 业务对象期间契约
 */
public interface IBOPeriodContract extends IBusinessLogicContract {

	/**
	 * 业务对象实例
	 * 
	 * @return
	 */
	IPeriodData getHost();
}
