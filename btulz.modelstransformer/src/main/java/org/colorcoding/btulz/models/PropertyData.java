package org.colorcoding.btulz.models;

import org.colorcoding.btulz.data.emDataSubType;
import org.colorcoding.btulz.data.emDataType;
import org.colorcoding.btulz.data.emPropertyType;
import org.colorcoding.btulz.data.emYesNo;

public class PropertyData extends Property implements IPropertyData {

	private emYesNo primaryKey;

	@Override
	public emYesNo isPrimaryKey() {
		if (this.primaryKey == null) {
			this.primaryKey = emYesNo.No;
		}
		return this.primaryKey;
	}

	@Override
	public void setPrimaryKey(emYesNo value) {
		this.primaryKey = value;
	}

	private emYesNo uniqueKey;

	@Override
	public emYesNo isUniqueKey() {
		if (this.uniqueKey == null) {
			this.uniqueKey = emYesNo.No;
		}
		return this.uniqueKey;
	}

	@Override
	public void setUniqueKey(emYesNo value) {
		this.uniqueKey = value;
	}

	private emDataType dataType;

	@Override
	public emDataType getDataType() {
		if (this.dataType == null) {
			this.dataType = emDataType.dt_Alphanumeric;
		}
		return this.dataType;
	}

	@Override
	public void setDataType(emDataType dataType) {
		this.dataType = dataType;
	}

	private emDataSubType dataSubType;

	@Override
	public emDataSubType getDataSubType() {
		if (this.dataSubType == null) {
			this.dataSubType = emDataSubType.st_None;
		}
		return this.dataSubType;
	}

	@Override
	public void setDataSubType(emDataSubType dataSubType) {
		this.dataSubType = dataSubType;
	}

	private int editSize;

	@Override
	public int getEditSize() {
		return this.editSize;
	}

	@Override
	public void setEditSize(int editSize) {
		this.editSize = editSize;
	}

	private String mapped;

	@Override
	public String getMapped() {
		return this.mapped;
	}

	@Override
	public void setMapped(String mapped) {
		this.mapped = mapped;
	}

	@Override
	public emPropertyType getPropertyType() {
		return emPropertyType.pt_Data;
	}

}
