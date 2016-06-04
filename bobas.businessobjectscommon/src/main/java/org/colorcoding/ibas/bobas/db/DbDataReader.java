package org.colorcoding.ibas.bobas.db;

import java.sql.ResultSet;

/**
 * 
 * @author Niuren.Zhu
 *
 */
public class DbDataReader extends DbDataReaderBase {

	public DbDataReader(ResultSet resultSet) {
		super(resultSet);
	}

	public IDataTable toDataTable() throws DbException {
		try {
			return null;
		} catch (Exception e) {
			throw new DbException(e.getMessage(), e);
		}
	}

	public IDataTable toDataTable(IDataTableConvertListener listener) throws DbException {
		// 如果是CPU密集型任务，就需要尽量压榨CPU，参考值可以设为 NCPU+1
		// 如果是IO密集型任务，参考值可以设置为2*NCPU
		// TODO Auto-generated method stub
		return null;
	}

}
