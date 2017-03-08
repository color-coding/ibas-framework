package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 事务类型
 * 
 * @author Niuren.Zhu
 *
 */
public enum TransactionType {
	/**
	 * 添加
	 */
	@Value("A")
	ADD,
	/**
	 * 更新
	 */
	@Value("U")
	UPDATE,
	/**
	 * 删除
	 */
	@Value("D")
	DELETE,
	// /**
	// * 关闭前
	// */
	// BEFORE_CLOSING,
	// /**
	// * 关闭后
	// */
	// CLOSED,
	// /**
	// * 取消前
	// */
	// BEFORE_CANCELING,
	// /**
	// * 取消后
	// */
	// CANCELD
	;
}
