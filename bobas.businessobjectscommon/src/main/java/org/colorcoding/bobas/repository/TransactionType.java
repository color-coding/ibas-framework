package org.colorcoding.bobas.repository;

import org.colorcoding.bobas.mapping.db.DbValue;

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
	@DbValue("A") Add,
	/**
	 * 更新
	 */
	@DbValue("U") Update,
	/**
	 * 删除
	 */
	@DbValue("D") Delete,
	// /**
	// * 关闭前
	// */
	// before_closing,
	// /**
	// * 关闭后
	// */
	// closed,
	// /**
	// * 取消前
	// */
	// before_canceling,
	// /**
	// * 取消后
	// */
	// canceld
	;
}
