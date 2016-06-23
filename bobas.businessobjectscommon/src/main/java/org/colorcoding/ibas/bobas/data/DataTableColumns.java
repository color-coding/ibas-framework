package org.colorcoding.ibas.bobas.data;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.util.ArrayList;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "DataTableColumns", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
@XmlSeeAlso({ DataTableColumn.class })
public class DataTableColumns extends ArrayList<IDataTableColumn> implements IDataTableColumns {

	/**
	 * 
	 */
	private static final long serialVersionUID = -592228599611799067L;

	private IDataTable table;

	public IDataTable getTable() {
		return table;
	}

	public void setTable(IDataTable table) {
		this.table = table;
	}

	protected DataTableColumns(IDataTable table) {
		this.setTable(table);
	}

	private void check() {
		if (this.getTable().getRows().size() > 0) {
			throw new RuntimeException(i18n.prop("msg_bobas_data_table_data_already_exists"));
		}
	}

	@Override
	public boolean add(IDataTableColumn e) {
		this.check();
		return super.add(e);
	}

	@Override
	public void add(int index, IDataTableColumn element) {
		this.check();
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends IDataTableColumn> c) {
		this.check();
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends IDataTableColumn> c) {
		this.check();
		return super.addAll(index, c);
	}

	@Override
	public void clear() {
		this.check();
		super.clear();
	}

	@Override
	public IDataTableColumn remove(int index) {
		this.check();
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		this.check();
		return super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		this.check();
		return super.removeAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super IDataTableColumn> filter) {
		this.check();
		return super.removeIf(filter);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		this.check();
		super.removeRange(fromIndex, toIndex);
	}

	@Override
	public void replaceAll(UnaryOperator<IDataTableColumn> operator) {
		this.check();
		super.replaceAll(operator);
	}

	@Override
	public IDataTableColumn set(int index, IDataTableColumn element) {
		this.check();
		return super.set(index, element);
	}

	/**
	 * 创建一列
	 * 
	 * @return
	 */
	public IDataTableColumn create() {
		this.check();
		DataTableColumn column = new DataTableColumn();
		if (super.add(column)) {
			column.setName(String.format("col_%s", this.size()));
		}
		return column;
	}

	@Override
	public IDataTableColumn create(String name, Class<?> type) {
		IDataTableColumn column = this.create();
		column.setName(name);
		column.setDataType(type);
		return column;
	}

	/**
	 * 通过 列名查找列
	 * 
	 * @param name
	 *            列名
	 * @return 返回查找的列
	 */
	public IDataTableColumn get(String name) {
		for (int i = 0; i < super.size(); i++) {
			if (super.get(i).getName().equals(name)) {
				return super.get(i);
			}
		}
		return null;
	}

}
