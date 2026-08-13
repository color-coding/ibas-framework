package org.colorcoding.ibas.bobas.db;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 数据库语句
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SqlStatement", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "SqlStatement", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class SqlStatement extends Serializable {

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

	@XmlElement(name = "Content")
	public String getContent() {
		if (this.content == null) {
			this.content = Strings.VALUE_EMPTY;
		}
		return this.content;
	}

	public void setContent(String value) {
		this.content = value;
	}

	/**
	 * 获取可执行的SQL语句。
	 * 基类直接返回Content，子类可覆盖此方法实现参数替换等逻辑。
	 *
	 * @return 可执行的SQL字符串
	 */
	public String executableSql() {
		return this.getContent();
	}

	@Override
	public String toString() {
		return Strings.format("{sql: %s...}", Strings.substring(this.getContent(), 26));
	}
}
