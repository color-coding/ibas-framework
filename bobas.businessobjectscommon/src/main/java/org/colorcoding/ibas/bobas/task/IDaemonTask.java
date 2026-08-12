package org.colorcoding.ibas.bobas.task;

/**
 * 后台任务接口
 *
 * @author Niuren.Zhu
 *
 */
public interface IDaemonTask extends Runnable {

	/**
	 * 获取任务名称
	 *
	 * @return 任务名称
	 */
	String getName();

	/**
	 * 获取运行间隔时间（秒），0表示仅执行一次
	 *
	 * @return 间隔秒数
	 */
	long getInterval();

	/**
	 * 是否激活的。默认实现：间隔大于等于0时激活；间隔为0的任务执行一次后由调度器移除
	 *
	 * @return true激活，false不激活
	 */
	default boolean isActivated() {
		return this.getInterval() >= 0;
	}

	/**
	 * 结束任务
	 */
	default void close() {
	}
}
