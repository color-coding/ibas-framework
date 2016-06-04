package org.colorcoding.ibas.btulz.data;

/**
 * 模型类型
 * 
 * @author Niuren.Zhu
 *
 */
public enum emModelType {
	/**
	 * 未指定
	 */
	mt_Unspecified,
	/**
	 * 主数据
	 */
	mt_MasterData,
	/**
	 * 主数据行
	 */
	mt_MasterDataLine,
	/**
	 * 单据
	 */
	mt_Document,
	/**
	 * 单据行
	 */
	mt_DocumentLine,
	/**
	 * 普通
	 */
	mt_Simple,
	/**
	 * 普通行
	 */
	mt_SimpleLine,
}
