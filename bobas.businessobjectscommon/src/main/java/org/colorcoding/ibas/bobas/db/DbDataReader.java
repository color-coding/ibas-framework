package org.colorcoding.ibas.bobas.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.IDataTableColumn;
import org.colorcoding.ibas.bobas.data.IDataTableRow;

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
					dtColumn.setDataType(BOFactory.create().getClass(metaData.getColumnClassName(i)));
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
			throw new DbException(e.getMessage(), e);
		}
	}

}
