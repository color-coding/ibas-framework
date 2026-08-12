package org.colorcoding.ibas.bobas.task;

/**
 * 单例任务
 * 
 * 适用于同一机器，多实例运行时保持任务单一，如：tomcat部署多个网站
 * 
 * 原理：
 * 
	 * 任务运行时通过临时目录中的文件锁保持同一机器上的跨进程互斥。
	 *
	 * 进程异常退出后，锁文件中的时间戳超过保持时间，其他进程可以接管任务。
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISingleDaemonTask extends IDaemonTask {

	/**
	 * 锁保持时间，单位秒。
	 * 
	 * @return
	 */
	long getKeepTime();

	/**
	 * 标记任务锁的签名，无锁不能运行
	 * 
	 * 锁以文件的形式存储在java临时目录，即：java.io.tmpdir
	 * 
	 * @return
	 */
	String getLockSignature();

}
