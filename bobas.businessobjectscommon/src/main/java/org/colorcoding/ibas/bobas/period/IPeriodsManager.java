package org.colorcoding.ibas.bobas.period;

/**
 * 期间管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPeriodsManager {

	/**
	 * 初始化
	 */
	void initialize();

	/**
	 * 应用期间
	 * 
	 * @param bo 数据
	 */
	void applyPeriod(IPeriodData bo) throws PeriodException;

	/**
	 * 检查数据期间
	 * 
	 * @param bo 数据
	 */
	void checkPeriod(IPeriodData bo) throws PeriodException;
}
