package org.colorcoding.ibas.bobas.core;

/**
 * 单例任务
 * 
 * 适用于同一机器，多实例运行时保持任务单一，如：tomcat部署多个网站
 * 
 * 原理：
 * 
 * 任务运行时在临时目录创建一个表示锁的文件，检测到文件则不再运行其他任务。
 * 
 * 文件的创建日期超过锁的时间，则认为无效，可以开始其他任务。
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
