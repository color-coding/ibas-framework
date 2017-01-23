package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.mapping.DbValue;

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
	@DbValue("A")
	ADD,
	/**
	 * 更新
	 */
	@DbValue("U")
	UPDATE,
	/**
	 * 删除
	 */
	@DbValue("D")
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
