package org.colorcoding.ibas.bobas.core;

/**
 * 守护进程
 * 
 * @author Niuren.Zhu
 *
 */
public interface IDaemon {
	/**
	 * 初始化
	 */
	void initialize();

	/**
	 * 添加任务
	 * 
	 * @param task
	 *            任务
	 * @return 分配的任务id
	 * @throws InvalidDaemonTask
	 */
	long add(IDaemonTask task) throws InvalidDaemonTask;

	/**
	 * 移出任务
	 * 
	 * @param taskId
	 *            分配的任务id
	 * @return true，成功；false，失败
	 */
	boolean remove(long taskId);

	/**
	 * 检查并运行
	 */
	void checkRun();
}
