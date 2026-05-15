package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.data.List;

/**
 * 操作结果
 * 
 * @author Niuren.Zhu
 *
 * @param <P> 返回的对象类型
 */
public interface IOperationResult<P> extends IOperationMessage {
	/**
	 * 返回对象
	 *
	 * @return 结果对象列表
	 */
	List<P> getResultObjects();

	/**
	 * 操作执行信息
	 *
	 * @return 信息列表
	 */
	List<IOperationInformation> getInformations();

	/**
	 * 复制数据
	 * 
	 * @param content 复制内容
	 * @return 当前实例
	 */
	IOperationResult<P> copy(IOperationResult<?> content);
}
