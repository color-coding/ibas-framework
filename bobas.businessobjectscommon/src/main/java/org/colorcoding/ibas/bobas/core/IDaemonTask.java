package org.colorcoding.ibas.bobas.core;

/**
 * 后台任务
 * 
 * @author Niuren.Zhu
 *
 */
public interface IDaemonTask extends Runnable {

	/**
	 * 是否激活的
	 * 
	 * @return
	 */
	boolean isActivated();

	/**
	 * 任务名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取-运行的间隔时间（秒）
	 * 
	 * 间隔0秒，则只执行一次
	 * 
	 * @return
	 */
	long getInterval();
}
