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
	 * 是否激活的。默认实现：间隔大于0时激活，间隔为0时仅执行一次不激活后续运行
	 *
	 * @return true激活，false不激活
	 */
	default boolean isActivated() {
		if (this.getInterval() <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * 结束任务
	 */
	default void close() {
	}
}