package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.exception.BasRuntimeException;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.Serializable;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.db.DataType;

/**
 * 查询条件
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Condition", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Condition", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class Condition extends Serializable implements ICondition {

	private static final long serialVersionUID = 3151721602767228504L;

	public Condition() {
	}

	public Condition(String alias, ConditionOperation operation, Object value) {
		this();
		this.setAlias(alias);
		this.setOperation(operation);
		this.setValue(value);
	}

	@XmlElement(name = "Alias")
	private String alias;

	@Override
	public final String getAlias() {
		if (this.alias == null) {
			this.alias = Strings.VALUE_EMPTY;
		}
		return this.alias;
	}

	@Override
	public final void setAlias(String value) {
		this.alias = value;
	}

	@Override
	public void setAlias(IPropertyInfo<?> property) {
		this.setAlias(property.getName());
	}

	@XmlElement(name = "BracketClose")
	private int bracketClose = 0;

	@Override
	public final int getBracketClose() {
		if (this.bracketClose < 0) {
			this.bracketClose = 0;
		}
		return this.bracketClose;
	}

	@Override
	public final void setBracketClose(int value) {
		this.bracketClose = value;
	}

	@Override
	public int addBracketClose() {
		this.setBracketClose(this.bracketClose + 1);
		return this.getBracketClose();
	}

	@XmlElement(name = "BracketOpen")
	private int bracketOpen = 0;

	@Override
	public final int getBracketOpen() {
		if (this.bracketOpen < 0) {
			this.bracketOpen = 0;
		}
		return this.bracketOpen;
	}

	@Override
	public final void setBracketOpen(int value) {
		this.bracketOpen = value;
	}

	@Override
	public int addBracketOpen() {
		this.setBracketOpen(this.bracketOpen + 1);
		return this.getBracketOpen();
	}

	@XmlElement(name = "ComparedAlias")
	private String comparedAlias;

	@Override
	public final String getComparedAlias() {
		if (this.comparedAlias == null) {
			this.comparedAlias = Strings.VALUE_EMPTY;
		}
		return this.comparedAlias;
	}

	@Override
	public final void setComparedAlias(String value) {
		this.comparedAlias = value;
	}

	@Override
	public void setComparedAlias(IPropertyInfo<?> property) {
		this.setComparedAlias(property.getName());
	}

	@XmlElement(name = "Value")
	private String value;

	@Override
	public final String getValue() {
		if (this.value == null) {
			this.value = Strings.VALUE_EMPTY;
		}
		return this.value;
	}

	@Override
	public final void setValue(Object value) {
		this.setValue(Strings.valueOf(value));
		if (value != null) {
			if (value instanceof BigDecimal) {
				this.aliasDataType = DataType.DECIMAL;
			} else if (value instanceof Integer || value instanceof Short) {
				this.aliasDataType = DataType.NUMERIC;
			} else if (value instanceof DateTime) {
				this.aliasDataType = DataType.DATE;
			}
		}
	}

	@Override
	public final void setValue(String value) {
		this.value = value;
		if (this.value == null) {
			// 设置null时，自动改变计算方式
			if (this.getOperation() == ConditionOperation.EQUAL) {
				this.setOperation(ConditionOperation.IS_NULL);
			} else if (this.getOperation() == ConditionOperation.NOT_EQUAL) {
				this.setOperation(ConditionOperation.NOT_NULL);
			}
		} else if (!Strings.isNullOrEmpty(this.value)) {
			// 非null时，自动改变计算方式
			if (this.getOperation() == ConditionOperation.IS_NULL) {
				this.setOperation(ConditionOperation.EQUAL);
			} else if (this.getOperation() == ConditionOperation.NOT_NULL) {
				this.setOperation(ConditionOperation.NOT_EQUAL);
			}
		}
	}

	@XmlElement(name = "Operation")
	private ConditionOperation operation = ConditionOperation.EQUAL;

	@Override
	public final ConditionOperation getOperation() {
		if (this.operation == null) {
			this.operation = ConditionOperation.EQUAL;
		}
		return this.operation;
	}

	@Override
	public final void setOperation(ConditionOperation value) {
		this.operation = value;
	}

	@XmlElement(name = "Relationship")
	private ConditionRelationship relationship = ConditionRelationship.AND;

	@Override
	public final ConditionRelationship getRelationship() {
		if (this.relationship == null) {
			this.relationship = ConditionRelationship.AND;
		}
		return this.relationship;
	}

	@Override
	public final void setRelationship(ConditionRelationship value) {
		this.relationship = value;
	}

	@XmlTransient // 运行过程值，序列化不用输出
	private DataType aliasDataType = null;

	@Override
	public DataType getAliasDataType() {
		if (this.aliasDataType == null) {
			this.aliasDataType = DataType.ALPHANUMERIC;
		}
		return this.aliasDataType;
	}

	@Override
	public void setAliasDataType(DataType value) {
		this.aliasDataType = value;
	}

	@Override
	public ICondition clone() {
		try {
			return (Condition) super.clone();
		} catch (Exception e) {
			throw new BasRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(64);
		stringBuilder.append("{condition: ");
		stringBuilder.append(this.getAlias());
		stringBuilder.append(" ");
		stringBuilder.append(this.getOperation());
		stringBuilder.append(" ");
		stringBuilder
				.append(this.getComparedAlias() != null && !this.getComparedAlias().isEmpty() ? this.getComparedAlias()
						: this.getValue());
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

}
