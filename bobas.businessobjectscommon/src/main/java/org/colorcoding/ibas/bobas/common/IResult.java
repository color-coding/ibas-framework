package org.colorcoding.ibas.bobas.common;

/**
 * 结果
 * 
 * @author Niuren.Zhu
 *
 */
public interface IResult {
	/**
	 * 结果编码
	 */
	int getResultCode();

	/**
	 * 结果描述
	 */
	String getMessage();
}
