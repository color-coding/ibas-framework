
package org.colorcoding.bobas.test.db;

import org.colorcoding.bobas.core.ObjectCloner;
import org.colorcoding.bobas.db.DataTable;
import org.colorcoding.bobas.db.DataTableColumn;
import org.colorcoding.bobas.db.DataTableRow;
import org.colorcoding.bobas.db.DataTableRows;

import junit.framework.TestCase;

public class testDataTable extends TestCase {

	public testDataTable() {
		super();
	}

	public void testFillDataTable() {

		DataTable dTable = new DataTable();
		for (int i = 0; i < 10; i++) {
			DataTableRow row = new DataTableRow();
			for (int j = 0; j < 10; j++) {
				DataTableColumn dataTableColumn = new DataTableColumn();
				dataTableColumn.setColumnName("col_" + String.valueOf(j));
				row.addColumn(dataTableColumn);
				row.setValue(j, j);
			}
			dTable.getRows().add(row);
		}
		System.out.println(ObjectCloner.toXmlString(dTable, true));
	}

	public void testDataTableToXML() {
		DataTableRow row = new DataTableRow();
		row.addColumn(new DataTableColumn("1"));
		row.addColumn(new DataTableColumn("2"));
		DataTableRows rows = new DataTableRows();
		rows.add(row);
		DataTable dt = new DataTable();
		dt.setRows(rows);
		dt.getRows().add(row);
		System.out.println(ObjectCloner.toXmlString(dt, true));

	}

}