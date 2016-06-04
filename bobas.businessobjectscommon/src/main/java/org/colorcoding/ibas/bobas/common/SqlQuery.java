package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SqlQuery", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "SqlQuery", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public class SqlQuery implements ISqlQuery {
	public SqlQuery() {
	}

	public SqlQuery(String sql) {
		this();
		this.setQueryString(sql);
	}

	private String queryString = "";

	@Override
	@XmlElement
	public final String getQueryString() {
		if (this.queryString == null && this.queryString.isEmpty()) {
			this.queryString = "";
		}
		return this.queryString;
	}

	@Override
	public final void setQueryString(String value) {
		this.queryString = value;
	}
}
