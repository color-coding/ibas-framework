package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 操作消息
 * @author Niuren.Zhu
 *
 */
public interface IOperationMessages {
	/**
	 * 结果标识
	 */
	String getSignID();

	/**
	 * 结果编码
	 */
	int getResultCode();

	/**
	 * 结果描述
	 */
	String getMessage();

	/**
	 * 结果时间
	 */
	DateTime getTime();

	/**
	 * 用户标识
	 */
	String getUserSign();

	/**
	 * 错误消息
	 */
	Exception getError();

	/**
	 * 复制消息
	 */
	void copy(IOperationMessages opMsg);
}
