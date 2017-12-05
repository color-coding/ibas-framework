package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.db.DataConvert;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 查询条件
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Condition", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Condition", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class Condition implements ICondition {

	public Condition() {

	}

	public Condition(String alias, ConditionOperation operation, Object value) {
		this();
		this.setAlias(alias);
		this.setOperation(operation);
		this.setValue(value);
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

	private int bracketClose = 0;

	@Override
	@XmlElement(name = "BracketClose")
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

	private int bracketOpen = 0;

	@Override
	@XmlElement(name = "BracketOpen")
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

	private String comparedAlias = "";

	@Override
	@XmlElement(name = "ComparedAlias")
	public final String getComparedAlias() {
		if (this.comparedAlias == null) {
			this.comparedAlias = "";
		}
		return this.comparedAlias;
	}

	@Override
	public final void setComparedAlias(String value) {
		this.comparedAlias = value;
	}

	private String value = "";

	@Override
	@XmlElement(name = "Value")
	public final String getValue() {
		if (this.value == null) {
			this.value = "";
		}
		return this.value;
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
		} else {
			// 非null时，自动改变计算方式
			if (this.getOperation() == ConditionOperation.IS_NULL) {
				this.setOperation(ConditionOperation.EQUAL);
			} else if (this.getOperation() == ConditionOperation.NOT_NULL) {
				this.setOperation(ConditionOperation.NOT_EQUAL);
			}
		}
	}

	@Override
	public final void setValue(Object value) {
		this.setValue(this.toValue(value));
	}

	protected String toValue(Object value) {
		if (value == null) {
			return null;
		}
		return DataConvert.toDbValue(value);
	}

	private ConditionOperation operation = ConditionOperation.EQUAL;

	@Override
	@XmlElement(name = "Operation")
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

	private ConditionRelationship relationship = ConditionRelationship.AND;

	@Override
	@XmlElement(name = "Relationship")
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

	private DbFieldType aliasDataType = DbFieldType.UNKNOWN;

	// @XmlElement(name = "AliasDataType")
	// 运行过程值，序列化不用输出
	@Override
	@XmlTransient
	public DbFieldType getAliasDataType() {
		if (this.aliasDataType == null) {
			this.aliasDataType = DbFieldType.UNKNOWN;
		}
		return this.aliasDataType;
	}

	@Override
	public void setAliasDataType(DbFieldType value) {
		this.aliasDataType = value;
	}

	private String remarks = "";

	@Override
	@XmlElement(name = "Remarks")
	public final String getRemarks() {
		if (this.remarks == null) {
			this.remarks = "";
		}
		return this.remarks;
	}

	@Override
	public final void setRemarks(String value) {
		this.remarks = value;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
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
