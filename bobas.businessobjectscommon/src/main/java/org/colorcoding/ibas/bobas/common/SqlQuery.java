package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.serialization.Serializable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SqlQuery", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "SqlQuery", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class SqlQuery extends Serializable implements ISqlQuery {

	private static final long serialVersionUID = -4755976465377026859L;
	private static final String SQL_EMPTY = "";

	/**
	 * 构造
	 */
	public SqlQuery() {
		this.setAllowRead(true);
		this.setAllowWrite(true);
	}

	/**
	 * 构造
	 * 
	 * @param sql
	 *            脚本内容
	 */
	public SqlQuery(String sql) {
		this();
		this.setQueryString(sql);
	}

	/**
	 * 构造
	 * 
	 * @param sql
	 *            脚本内容
	 * @param read
	 *            允许读取数据
	 * @param write
	 *            允许写入数据
	 */
	public SqlQuery(String sql, boolean read, boolean write) {
		this(sql);
		this.setAllowRead(read);
		this.setAllowWrite(write);
	}

	private String queryString;

	@Override
	@XmlElement(name = "QueryString")
	public final String getQueryString() {
		if (this.queryString == null) {
			this.queryString = SQL_EMPTY;
		}
		return this.queryString;
	}

	@Override
	public final void setQueryString(String value) {
		this.queryString = value;
	}

	private boolean allowRead;

	@Override
	@XmlElement(name = "AllowRead")
	public final boolean isAllowRead() {
		return allowRead;
	}

	public final void setAllowRead(boolean value) {
		this.allowRead = value;
	}

	private boolean allowWrite;

	@Override
	@XmlElement(name = "AllowWrite")
	public final boolean isAllowWrite() {
		return allowWrite;
	}

	public final void setAllowWrite(boolean value) {
		this.allowWrite = value;
	}

	@Override
	public String toString() {
		return this.getQueryString();
	}
}
