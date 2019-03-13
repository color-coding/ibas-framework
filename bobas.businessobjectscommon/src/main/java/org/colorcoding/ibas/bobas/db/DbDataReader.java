package org.colorcoding.ibas.bobas.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.IDataTableColumn;
import org.colorcoding.ibas.bobas.data.IDataTableRow;

/**
 * 
 * @author Niuren.Zhu
 *
 */
public class DbDataReader implements IDbDataReader {

	private DbDataReader() {
	}

	public DbDataReader(ResultSet resultSet) {
		this();
		this.resultSet = resultSet;
	}

	private ResultSet resultSet = null;

	@Override
	public void close() throws DbException {
		try {
			this.resultSet.close();
			this.resultSet = null;
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public int getRow() throws DbException {
		try {
			return this.resultSet.getRow();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public int getColumnCount() throws DbException {
		try {
			return this.resultSet.getMetaData().getColumnCount();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public ResultSetMetaData getMetaData() throws DbException {
		try {
			return this.resultSet.getMetaData();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean isClosed() throws DbException {
		try {
			return this.resultSet.isClosed();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean isBeforeFirst() throws DbException {
		try {
			return this.resultSet.isBeforeFirst();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean isFirst() throws DbException {
		try {
			return this.resultSet.isFirst();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean isLast() throws DbException {
		try {
			return this.resultSet.isLast();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean last() throws DbException {
		try {
			return this.resultSet.last();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean next() throws DbException {
		try {
			return this.resultSet.next();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean previous() throws DbException {
		try {
			return this.resultSet.previous();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public void setFetchSize(int rows) throws DbException {
		try {
			this.resultSet.setFetchSize(rows);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public int getFetchSize() throws DbException {
		try {
			return this.resultSet.getFetchSize();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean absolute(int row) throws DbException {
		try {
			return this.resultSet.absolute(row);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public int findColumn(String columnLabel) throws DbException {
		try {
			return this.resultSet.findColumn(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean first() throws DbException {
		try {
			return this.resultSet.first();
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean getBoolean(int columnIndex) throws DbException {
		try {
			return this.resultSet.getBoolean(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public boolean getBoolean(String columnLabel) throws DbException {
		try {
			return this.resultSet.getBoolean(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public byte[] getBytes(int columnIndex) throws DbException {
		try {
			return this.resultSet.getBytes(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public byte[] getBytes(String columnLabel) throws DbException {
		try {
			return this.resultSet.getBytes(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public double getDouble(int columnIndex) throws DbException {
		try {
			return this.resultSet.getDouble(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public double getDouble(String columnLabel) throws DbException {
		try {
			return this.resultSet.getDouble(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public float getFloat(int columnIndex) throws DbException {
		try {
			return this.resultSet.getFloat(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public float getFloat(String columnLabel) throws DbException {
		try {
			return this.resultSet.getFloat(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public int getInt(int columnIndex) throws DbException {
		try {
			return this.resultSet.getInt(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public int getInt(String columnLabel) throws DbException {
		try {
			return this.resultSet.getInt(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public long getLong(int columnIndex) throws DbException {
		try {
			return this.resultSet.getLong(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public long getLong(String columnLabel) throws DbException {
		try {
			return this.resultSet.getLong(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public short getShort(int columnIndex) throws DbException {
		try {
			return this.resultSet.getShort(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public short getShort(String columnLabel) throws DbException {
		try {
			return this.resultSet.getShort(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public String getString(int columnIndex) throws DbException {
		try {
			return this.resultSet.getString(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public String getString(String columnLabel) throws DbException {
		try {
			return this.resultSet.getString(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public DateTime getDateTime(int columnIndex) throws DbException {
		try {
			return new DateTime(this.resultSet.getDate(columnIndex).getTime());
		} catch (NullPointerException e) {
			return DateTime.MIN_VALUE;
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public DateTime getDateTime(String columnLabel) throws DbException {
		try {
			return new DateTime(this.resultSet.getDate(columnLabel).getTime());
		} catch (NullPointerException e) {
			return DateTime.MIN_VALUE;
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public BigDecimal getDecimal(int columnIndex) throws DbException {
		try {
			return this.resultSet.getBigDecimal(columnIndex);
		} catch (NullPointerException e) {
			return Decimal.ZERO;
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public BigDecimal getDecimal(String columnLabel) throws DbException {
		try {
			return this.resultSet.getBigDecimal(columnLabel);
		} catch (NullPointerException e) {
			return Decimal.ZERO;
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public Object getObject(int columnIndex) throws DbException {
		try {
			return this.resultSet.getObject(columnIndex);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	@Override
	public Object getObject(String columnLabel) throws DbException {
		try {
			return this.resultSet.getObject(columnLabel);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

	public IDataTable toDataTable() throws DbException {
		try {
			DataTable dataTable = new DataTable();
			ResultSetMetaData metaData = this.getMetaData();
			if (metaData.getColumnCount() > 0) {
				// 设置表名
				dataTable.setName(metaData.getTableName(1));
				// 创建列
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					IDataTableColumn dtColumn = dataTable.getColumns().create();
					String name = metaData.getColumnName(i);
					if (name == null || name.isEmpty()) {
						name = String.format("col_%s", i);
					}
					dtColumn.setName(name);
					if (!dtColumn.getName().equalsIgnoreCase(metaData.getColumnLabel(i))) {
						dtColumn.setDescription(metaData.getColumnLabel(i));
					}
					dtColumn.setDataType(BOFactory.create().loadClass(metaData.getColumnClassName(i)));
				}
				// 添加行数据
				while (this.next()) {
					IDataTableRow row = dataTable.getRows().create();
					// 行的每列赋值
					for (int i = 0; i < dataTable.getColumns().size(); i++) {
						row.setValue(i, this.getObject(i + 1));
					}
				}
			}
			return dataTable;
		} catch (Exception e) {
			throw new DbException(e);
		}
	}

}
