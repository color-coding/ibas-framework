package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.db.DataConvert;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 查询条件
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Condition", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Condition", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public class Condition implements ICondition {

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

	private int bracketCloseNum = 0;

	@Override
	@XmlElement(name = "BracketCloseNum")
	public final int getBracketCloseNum() {
		if (this.bracketCloseNum < 0) {
			this.bracketCloseNum = 0;
		}
		return this.bracketCloseNum;
	}

	@Override
	public final void setBracketCloseNum(int value) {
		this.bracketCloseNum = value;
	}

	private int bracketOpenNum = 0;

	@Override
	@XmlElement(name = "BracketOpenNum")
	public final int getBracketOpenNum() {
		if (this.bracketOpenNum < 0) {
			this.bracketOpenNum = 0;
		}
		return this.bracketOpenNum;
	}

	@Override
	public final void setBracketOpenNum(int value) {
		this.bracketOpenNum = value;
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

	private String condVal = "";

	@Override
	@XmlElement(name = "CondVal")
	public final String getCondVal() {
		if (this.condVal == null) {
			this.condVal = "";
		}
		return this.condVal;
	}

	@Override
	public final void setCondVal(String value) {
		this.condVal = value;
	}

	@Override
	public final void setCondVal(Object value) {
		this.condVal = this.toValue(value);
	}

	protected String toValue(Object value) {
		if (value == null) {
			return "";
		}
		return DataConvert.toDbValue(value);
	}

	private ConditionOperation operation = ConditionOperation.co_EQUAL;

	@Override
	@XmlElement(name = "Operation")
	public final ConditionOperation getOperation() {
		if (this.operation == null) {
			this.operation = ConditionOperation.co_EQUAL;
		}
		return this.operation;
	}

	@Override
	public final void setOperation(ConditionOperation value) {
		this.operation = value;
	}

	private ConditionRelationship relationship = ConditionRelationship.cr_AND;

	@Override
	@XmlElement(name = "Relationship")
	public final ConditionRelationship getRelationship() {
		if (this.relationship == null) {
			this.relationship = ConditionRelationship.cr_AND;
		}
		return this.relationship;
	}

	@Override
	public final void setRelationship(ConditionRelationship value) {
		this.relationship = value;
	}

	private DbFieldType aliasDataType = DbFieldType.db_Unknown;

	// @XmlElement(name = "AliasDataType")
	// 运行过程值，序列化不用输出
	@Override
	@XmlTransient
	public DbFieldType getAliasDataType() {
		if (this.aliasDataType == null) {
			this.aliasDataType = DbFieldType.db_Unknown;
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

}
