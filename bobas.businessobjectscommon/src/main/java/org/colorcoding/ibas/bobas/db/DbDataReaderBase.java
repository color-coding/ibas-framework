package org.colorcoding.ibas.bobas.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;

/**
 * 基本数据读取者
 * 
 * 仅包含数据读取
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class DbDataReaderBase implements IDbDataReader {

	private DbDataReaderBase() {
	}

	public DbDataReaderBase(ResultSet resultSet) {
		this();
		this.resultSet = resultSet;
	}

	private ResultSet resultSet = null;

	@Override
	public void close() throws DbException {
		try {
			this.resultSet.close();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public int getRow() throws DbException {
		try {
			return this.resultSet.getRow();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public int getColumnCount() throws DbException {
		try {
			return this.resultSet.getMetaData().getColumnCount();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public ResultSetMetaData getMetaData() throws DbException {
		try {
			return this.resultSet.getMetaData();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isClosed() throws DbException {
		try {
			return this.resultSet.isClosed();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isFirst() throws DbException {
		try {
			return this.resultSet.isFirst();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isLast() throws DbException {
		try {
			return this.resultSet.isLast();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean last() throws DbException {
		try {
			return this.resultSet.last();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean next() throws DbException {
		try {
			return this.resultSet.next();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean previous() throws DbException {
		try {
			return this.resultSet.previous();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public void setFetchSize(int rows) throws DbException {
		try {
			this.resultSet.setFetchSize(rows);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public int getFetchSize() throws DbException {
		try {
			return this.resultSet.getFetchSize();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean absolute(int row) throws DbException {
		try {
			return this.resultSet.absolute(row);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public int findColumn(String columnLabel) throws DbException {
		try {
			return this.resultSet.findColumn(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean first() throws DbException {
		try {
			return this.resultSet.first();
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean getBoolean(int columnIndex) throws DbException {
		try {
			return this.resultSet.getBoolean(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public boolean getBoolean(String columnLabel) throws DbException {
		try {
			return this.resultSet.getBoolean(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] getBytes(int columnIndex) throws DbException {
		try {
			return this.resultSet.getBytes(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] getBytes(String columnLabel) throws DbException {
		try {
			return this.resultSet.getBytes(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public double getDouble(int columnIndex) throws DbException {
		try {
			return this.resultSet.getDouble(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public double getDouble(String columnLabel) throws DbException {
		try {
			return this.resultSet.getDouble(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public float getFloat(int columnIndex) throws DbException {
		try {
			return this.resultSet.getFloat(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public float getFloat(String columnLabel) throws DbException {
		try {
			return this.resultSet.getFloat(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public int getInt(int columnIndex) throws DbException {
		try {
			return this.resultSet.getInt(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public int getInt(String columnLabel) throws DbException {
		try {
			return this.resultSet.getInt(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public long getLong(int columnIndex) throws DbException {
		try {
			return this.resultSet.getLong(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public long getLong(String columnLabel) throws DbException {
		try {
			return this.resultSet.getLong(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public short getShort(int columnIndex) throws DbException {
		try {
			return this.resultSet.getShort(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public short getShort(String columnLabel) throws DbException {
		try {
			return this.resultSet.getShort(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public String getString(int columnIndex) throws DbException {
		try {
			return this.resultSet.getString(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public String getString(String columnLabel) throws DbException {
		try {
			return this.resultSet.getString(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public DateTime getDateTime(int columnIndex) throws DbException {
		try {
			return new DateTime(this.resultSet.getDate(columnIndex).getTime());
		} catch (NullPointerException e) {
			return DateTime.minValue;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public DateTime getDateTime(String columnLabel) throws DbException {
		try {
			return new DateTime(this.resultSet.getDate(columnLabel).getTime());
		} catch (NullPointerException e) {
			return DateTime.minValue;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public Decimal getDecimal(int columnIndex) throws DbException {
		try {
			return new Decimal(this.resultSet.getBigDecimal(columnIndex));
		} catch (NullPointerException e) {
			return Decimal.ZERO;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public Decimal getDecimal(String columnLabel) throws DbException {
		try {
			return new Decimal(this.resultSet.getBigDecimal(columnLabel));
		} catch (NullPointerException e) {
			return Decimal.ZERO;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public Object getObject(int columnIndex) throws DbException {
		try {
			return this.resultSet.getObject(columnIndex);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public Object getObject(String columnLabel) throws DbException {
		try {
			return this.resultSet.getObject(columnLabel);
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

}
