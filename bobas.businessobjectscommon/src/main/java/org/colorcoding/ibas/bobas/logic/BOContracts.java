package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOSeriesKey;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.logic.common.IBOPrimaryKeyContract;

class BOPrimaryKeyContract implements IBOPrimaryKeyContract {

	public BOPrimaryKeyContract(IBusinessObject host) {
		this.host = host;
	}

	private IBusinessObject host;

	@Override
	public String getIdentifiers() {
		return this.host.getIdentifiers();
	}

	@Override
	public String getObjectCode() {
		if (this.host instanceof IBOStorageTag) {
			return ((IBOStorageTag) this.host).getObjectCode();
		}
		return Strings.VALUE_EMPTY;
	}

	@Override
	public Integer getSeries() {
		if (this.host instanceof IBOSeriesKey) {
			return ((IBOSeriesKey) this.host).getSeries();
		}
		return Numbers.INTEGER_VALUE_ZERO;
	}

	@Override
	public boolean setPrimaryKey(Object... keys) {
		if (this.host instanceof IBODocument) {
			IBODocument data = (IBODocument) this.host;
			data.setDocEntry((Integer) keys[0]);
			return true;
		} else if (this.host instanceof IBODocumentLine) {
			IBODocumentLine data = (IBODocumentLine) this.host;
			if (keys.length == 1) {
				data.setLineId((Integer) keys[0]);
				return true;
			} else if (keys.length == 2) {
				data.setDocEntry((Integer) keys[0]);
				data.setLineId((Integer) keys[1]);
				return true;
			}
		} else if (this.host instanceof IBOMasterData) {
			IBOMasterData data = (IBOMasterData) this.host;
			data.setDocEntry((Integer) keys[0]);
			return true;
		} else if (this.host instanceof IBOMasterDataLine) {
			IBOMasterDataLine data = (IBOMasterDataLine) this.host;
			if (keys.length == 1) {
				data.setLineId((Integer) keys[0]);
				return true;
			}
		} else if (this.host instanceof IBOSimple) {
			IBOSimple data = (IBOSimple) this.host;
			data.setObjectKey((Integer) keys[0]);
			return true;
		} else if (this.host instanceof IBOSimpleLine) {
			IBOSimpleLine data = (IBOSimpleLine) this.host;
			if (keys.length == 1) {
				data.setLineId((Integer) keys[0]);
				return true;
			} else if (keys.length == 2) {
				data.setObjectKey((Integer) keys[0]);
				data.setLineId((Integer) keys[1]);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setSeriesKey(String key) {
		if (this.host instanceof IBOSeriesKey) {
			((IBOSeriesKey) this.host).setSeriesValue(key);
			return true;
		}
		return false;
	}
}
