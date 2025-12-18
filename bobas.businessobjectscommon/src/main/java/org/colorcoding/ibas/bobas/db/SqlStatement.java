package org.colorcoding.ibas.bobas.db;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 数据库语句
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SqlStatement", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "SqlStatement", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class SqlStatement extends Serializable implements ISqlStatement {

	private static final long serialVersionUID = -4755976465377026859L;

	class Parameter {

		public DbFieldType targetType;

		public Object value;

		public String name;

		@Override
		public String toString() {
			return Strings.format("{parameter: %s}", value);
		}
	}

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
	public String getContent() {
		if (this.content == null) {
			this.content = Strings.VALUE_EMPTY;
		}
		return this.content;
	}

	@Override
	public void setContent(String value) {
		this.content = value;
	}

	@Override
	public String toString() {
		return Strings.format("{sql: %s...}", Strings.substring(this.getContent(), 26));
	}

	private Map<Integer, Parameter> parameters;

	Map<Integer, Parameter> getParameters() {
		if (this.parameters == null) {
			this.parameters = new HashMap<Integer, Parameter>();
		}
		return this.parameters;
	}

	@Override
	public void clearParameters() {
		if (this.parameters != null) {
			this.parameters.clear();
		}
	}

	@Override
	public void setObject(int parameterIndex, Object value, DbFieldType targetType) {
		Parameter parameter = new Parameter();
		parameter.targetType = targetType;
		parameter.value = value;
		this.getParameters().put(parameterIndex, parameter);
	}

	@Override
	public void setObject(int parameterIndex, Object value) {
		this.setObject(parameterIndex, value, DbFieldType.UNKNOWN);
	}

	public void setObject(Iterable<ICondition> conditions) {
		if (conditions != null) {
			int index = 0;
			for (ICondition condition : conditions) {
				if (condition == null) {
					continue;
				}
				// 字段间比较，不设置参数值
				if (!Strings.isNullOrEmpty(condition.getComparedAlias())) {
					continue;
				}
				// 空值比较，不设置参数值
				if (condition.getOperation() == ConditionOperation.IS_NULL
						|| condition.getOperation() == ConditionOperation.NOT_NULL) {
					continue;
				}
				if (condition.getOperation() == ConditionOperation.CONTAIN
						|| condition.getOperation() == ConditionOperation.NOT_CONTAIN) {
					this.setObject(index, "%" + condition.getValue() + "%", condition.getAliasDataType());
				} else if (condition.getOperation() == ConditionOperation.START) {
					this.setObject(index, condition.getValue() + "%", condition.getAliasDataType());
				} else if (condition.getOperation() == ConditionOperation.END) {
					this.setObject(index, "%" + condition.getValue(), condition.getAliasDataType());
				} else {
					this.setObject(index, condition.getValue(), condition.getAliasDataType());
				}
				index++;
			}
		}
	}

}
