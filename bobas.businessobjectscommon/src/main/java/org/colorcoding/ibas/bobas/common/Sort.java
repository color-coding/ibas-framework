package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 查询排序
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Sort", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Sort", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class Sort extends Serializable implements ISort, Cloneable {

	private static final long serialVersionUID = 2186775431210517706L;

	public Sort() {

	}

	public Sort(String alias, SortType sortType) {
		this();
		this.setAlias(alias);
		this.setSortType(sortType);
	}

	@XmlElement(name = "Alias")
	private String alias = "";

	@Override
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

	@XmlElement(name = "SortType")
	private SortType sortType = SortType.ASCENDING;

	@Override
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
	public ISort clone() {
		try {
			return (Sort) super.clone();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
