package org.colorcoding.ibas.bobas.db;

import java.sql.ResultSetMetaData;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.IDataTable;

/**
 * 数据库数据读取
 */
public interface IDbDataReader {
	/**
	 * 转换为数据表
	 */
	IDataTable toDataTable() throws DbException;

	/**
	 * 关闭结果集
	 */
	void close() throws DbException;

	/**
	 * 获取列数
	 * 
	 * @return
	 * @throws DbException
	 */
	int getColumnCount() throws DbException;

	/**
	 * 获取结果集说明
	 * 
	 * @return
	 * @throws DbException
	 */
	ResultSetMetaData getMetaData() throws DbException;

	/**
	 * 获取当前行号
	 * 
	 * @return
	 * @throws DbException
	 */
	int getRow() throws DbException;

	/**
	 * 是否关闭
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean isClosed() throws DbException;

	/**
	 * 是否第一行
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean isFirst() throws DbException;

	/**
	 * 是否最后一行
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean isLast() throws DbException;

	/**
	 * 移到最后一行
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean last() throws DbException;

	/**
	 * 移到下一行
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean next() throws DbException;

	/**
	 * 移到上一行
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean previous() throws DbException;

	/**
	 * 设置查询大小
	 * 
	 * @param rows
	 * @throws DbException
	 */
	void setFetchSize(int rows) throws DbException;

	/**
	 * 获取查询大小
	 * 
	 * @return
	 * @throws DbException
	 */
	int getFetchSize() throws DbException;

	/**
	 * 定位到某行
	 * 
	 * @param row
	 *            行数
	 * @return
	 * @throws DbException
	 */
	boolean absolute(int row) throws DbException;

	/**
	 * 获取列索引
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	int findColumn(String columnLabel) throws DbException;

	/**
	 * 移到第一行
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean first() throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	Object getObject(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	Object getObject(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	Decimal getDecimal(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	Decimal getDecimal(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	boolean getBoolean(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	boolean getBoolean(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	byte[] getBytes(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	byte[] getBytes(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	DateTime getDateTime(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	DateTime getDateTime(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	double getDouble(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	double getDouble(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	float getFloat(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	float getFloat(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	int getInt(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	int getInt(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	long getLong(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	long getLong(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	short getShort(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	short getShort(String columnLabel) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnIndex
	 *            列索引
	 * @return
	 * @throws DbException
	 */
	String getString(int columnIndex) throws DbException;

	/**
	 * 获取值
	 * 
	 * @param columnLabel
	 *            列名称
	 * @return
	 * @throws DbException
	 */
	String getString(String columnLabel) throws DbException;

}
