package org.colorcoding.ibas.bobas.db;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SqlStatement", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "SqlStatement", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class SqlStatement extends Serializable implements ISqlStatement {

	private static final long serialVersionUID = -4755976465377026859L;

	/**
	 * 构造
	 */
	public SqlStatement() {
	}

	/**
	 * 构造
	 * 
	 * @param sql 脚本内容
	 */
	public SqlStatement(String sql) {
		this();
		this.setContent(sql);
	}

	private String content;

	@Override
	@XmlElement(name = "Content")
	public final String getContent() {
		if (this.content == null) {
			this.content = Strings.VALUE_EMPTY;
		}
		return this.content;
	}

	@Override
	public final void setContent(String value) {
		this.content = value;
	}

	@Override
	public String toString() {
		return Strings.format("{SQL: %s}", Strings.substring(this.getContent(), 8));
	}
}
