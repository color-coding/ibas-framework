package org.colorcoding.ibas.bobas.data;

/**
 * 数据表行
 * 
 * @author Niuren.Zhu
 *
 */
public interface IDataTableRow {
	/**
	 * 获取值
	 * 
	 * @param col
	 *            列索引
	 * @return 值
	 */
	Object getValue(int col);

	/**
	 * 设置-值
	 * 
	 * @param col
	 *            列索引
	 * @param value
	 *            值
	 */
	void setValue(int col, Object value);

	/**
	 * 获取值
	 * 
	 * @param col
	 *            列名称
	 * @return 值
	 */
	Object getValue(String col);

	/**
	 * 设置-值
	 * 
	 * @param col
	 *            列名称
	 * @param value
	 *            值
	 */
	void setValue(String col, Object value);

	/**
	 * 获取值
	 * 
	 * @param col
	 *            列
	 * @return 值
	 */
	Object getValue(IDataTableColumn col);

	/**
	 * 设置-值
	 * 
	 * @param col
	 *            列
	 * @param value
	 *            值
	 */
	void setValue(IDataTableColumn col, Object value);
}
