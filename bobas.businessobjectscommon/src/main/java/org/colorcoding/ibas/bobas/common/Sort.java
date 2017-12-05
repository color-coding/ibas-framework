package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 查询排序
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Sort", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Sort", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class Sort implements ISort {
	public Sort() {

	}

	public Sort(String alias, SortType sortType) {
		this();
		this.setAlias(alias);
		this.setSortType(sortType);
	}

	private String alias = "";

	@Override
	@XmlElement(name = "Alias")
	public final String getAlias() {
		if (this.alias == null) {
			this.alias = "";
		}
		return this.alias;
	}

	@Override
	public final void setAlias(String value) {
		this.alias = value;
	}

	private SortType sortType = SortType.ASCENDING;

	@Override
	@XmlElement(name = "SortType")
	public final SortType getSortType() {
		if (this.sortType == null) {
			this.sortType = SortType.ASCENDING;
		}
		return this.sortType;
	}

	@Override
	public final void setSortType(SortType value) {
		this.sortType = value;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{sort: ");
		stringBuilder.append(this.getAlias());
		stringBuilder.append(" ");
		stringBuilder.append(this.getSortType());
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
