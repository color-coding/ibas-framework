package org.colorcoding.ibas.bobas.period;

/**
 * 期间管理器基类，提供期间数据的应用和检查
 *
 * @author Niuren.Zhu
 *
 */
public abstract class PeriodsManager {

	/** 初始化期间数据 */
	public abstract void initialize();

	/**
	 * 应用期间数据到业务对象（设置期间编号等）
	 *
	 * @param bo 业务对象
	 * @throws PeriodException 期间数据无效
	 */
	public abstract void applyPeriod(IPeriodData bo) throws PeriodException;

	/**
	 * 检查业务对象的期间状态是否允许操作
	 *
	 * @param bo 业务对象
	 * @throws PeriodException 期间已锁定或不存在
	 */
	public abstract void checkPeriod(IPeriodData bo) throws PeriodException;
}