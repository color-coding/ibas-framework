package org.colorcoding.ibas.bobas.period;

/**
 * 期间管理员
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class PeriodsManager {

	/**
	 * 初始化期间数据
	 */
	public abstract void initialize();

	/**
	 * 应用期间数据
	 * 
	 * @param bo 被应用对象
	 * @throws PeriodException
	 */
	public abstract void applyPeriod(IPeriodData bo) throws PeriodException;

	/**
	 * 检查期间状态
	 * 
	 * @param bo 数据源
	 * @throws PeriodException
	 */
	public abstract void checkPeriod(IPeriodData bo) throws PeriodException;
}
