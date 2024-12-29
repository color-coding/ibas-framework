package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IApprovalData;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOSeriesKey;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IPeriodData;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.logic.common.IBOApprovalContract;
import org.colorcoding.ibas.bobas.logic.common.IBOInstanceLogContract;
import org.colorcoding.ibas.bobas.logic.common.IBOPeriodContract;
import org.colorcoding.ibas.bobas.logic.common.IBOPrimaryKeyContract;
import org.colorcoding.ibas.bobas.logic.common.IBORulesContract;
import org.colorcoding.ibas.bobas.logic.common.IBOStorageTagContract;

class BOContract<T> implements IBusinessLogicContract {
	public BOContract(T host) {
		this.setHost(host);
	}

	private T host;

	public synchronized final T getHost() {
		return host;
	}

	private synchronized final void setHost(T host) {
		this.host = host;
	}

	@Override
	public String getIdentifiers() {
		if (this.getHost() instanceof IBusinessObject) {
			return ((IBusinessObject) this.getHost()).getIdentifiers();
		}
		throw new RuntimeException(Strings.format("Method not implemented."));
	}
}

class BOPrimaryKeyContract extends BOContract<IBusinessObject> implements IBOPrimaryKeyContract {

	public BOPrimaryKeyContract(IBusinessObject host) {
		super(host);
	}

	@Override
	public String getObjectCode() {
		if (this.getHost() instanceof IBOStorageTag) {
			return ((IBOStorageTag) this.getHost()).getObjectCode();
		}
		return Strings.VALUE_EMPTY;
	}

	@Override
	public Integer getSeries() {
		if (this.getHost() instanceof IBOSeriesKey) {
			return ((IBOSeriesKey) this.getHost()).getSeries();
		}
		return Numbers.INTEGER_VALUE_ZERO;
	}

	@Override
	public boolean setPrimaryKey(Object... keys) {
		if (this.getHost() instanceof IBODocument) {
			IBODocument data = (IBODocument) this.getHost();
			data.setDocEntry((Integer) keys[0]);
			return true;
		} else if (this.getHost() instanceof IBODocumentLine) {
			IBODocumentLine data = (IBODocumentLine) this.getHost();
			if (keys.length == 1) {
				data.setLineId((Integer) keys[0]);
				return true;
			} else if (keys.length == 2) {
				data.setDocEntry((Integer) keys[0]);
				data.setLineId((Integer) keys[1]);
				return true;
			}
		} else if (this.getHost() instanceof IBOMasterData) {
			IBOMasterData data = (IBOMasterData) this.getHost();
			data.setDocEntry((Integer) keys[0]);
			return true;
		} else if (this.getHost() instanceof IBOMasterDataLine) {
			IBOMasterDataLine data = (IBOMasterDataLine) this.getHost();
			if (keys.length == 1) {
				data.setLineId((Integer) keys[0]);
				return true;
			}
		} else if (this.getHost() instanceof IBOSimple) {
			IBOSimple data = (IBOSimple) this.getHost();
			data.setObjectKey((Integer) keys[0]);
			return true;
		} else if (this.getHost() instanceof IBOSimpleLine) {
			IBOSimpleLine data = (IBOSimpleLine) this.getHost();
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
		if (this.getHost() instanceof IBOSeriesKey) {
			((IBOSeriesKey) this.getHost()).setSeriesValue(key);
			return true;
		}
		return false;
	}
}

class BOStorageTagContract extends BOContract<IBOStorageTag> implements IBOStorageTagContract {

	public BOStorageTagContract(IBOStorageTag host) {
		super(host);
	}

	@Override
	public String getObjectCode() {
		return this.getHost().getObjectCode();
	}

	@Override
	public DateTime getCreateDate() {
		return this.getHost().getCreateDate();
	}

	@Override
	public void setCreateDate(DateTime value) {
		this.getHost().setCreateDate(value);

	}

	@Override
	public Short getCreateTime() {
		return this.getHost().getCreateTime();
	}

	@Override
	public void setCreateTime(Short value) {
		this.getHost().setCreateTime(value);

	}

	@Override
	public DateTime getUpdateDate() {
		return this.getHost().getUpdateDate();
	}

	@Override
	public void setUpdateDate(DateTime value) {
		this.getHost().setUpdateDate(value);

	}

	@Override
	public Short getUpdateTime() {
		return this.getHost().getUpdateTime();
	}

	@Override
	public void setUpdateTime(Short value) {
		this.getHost().setUpdateTime(value);

	}

	@Override
	public Integer getLogInst() {
		return this.getHost().getLogInst();
	}

	@Override
	public void setLogInst(Integer value) {
		this.getHost().setLogInst(value);
	}

	@Override
	public Integer getCreateUserSign() {
		return this.getHost().getCreateUserSign();
	}

	@Override
	public void setCreateUserSign(Integer value) {
		this.getHost().setCreateUserSign(value);

	}

	@Override
	public Integer getUpdateUserSign() {
		return this.getHost().getUpdateUserSign();
	}

	@Override
	public void setUpdateUserSign(Integer value) {
		this.getHost().setUpdateUserSign(value);
	}

	@Override
	public String getCreateActionId() {
		return this.getHost().getCreateActionId();
	}

	@Override
	public void setCreateActionId(String value) {
		this.getHost().setCreateActionId(value);
	}

	@Override
	public String getUpdateActionId() {
		return this.getHost().getUpdateActionId();
	}

	@Override
	public void setUpdateActionId(String value) {
		this.getHost().setUpdateActionId(value);
	}

	@Override
	public String getDataSource() {
		return this.getHost().getDataSource();
	}

	@Override
	public void setDataSource(String value) {
		this.getHost().setDataSource(value);
	}

}

class BOPeriodContract extends BOContract<IPeriodData> implements IBOPeriodContract {

	public BOPeriodContract(IPeriodData host) {
		super(host);
	}

	@Override
	public String getObjectCode() {
		return this.getHost().getObjectCode();
	}

	@Override
	public DateTime getDocumentDate() {
		return this.getHost().getDocumentDate();
	}

	@Override
	public Integer getPeriod() {
		return this.getHost().getPeriod();
	}

	@Override
	public void setPeriod(Integer value) {
		this.getHost().setPeriod(value);
	}

}

class BOApprovalContract extends BOContract<IApprovalData> implements IBOApprovalContract {

	public BOApprovalContract(IApprovalData host) {
		super(host);
	}

	@Override
	public String getObjectCode() {
		return this.getHost().getObjectCode();
	}

	@Override
	public Integer getDataOwner() {
		return this.getHost().getDataOwner();
	}

	@Override
	public emApprovalStatus getApprovalStatus() {
		return this.getHost().getApprovalStatus();
	}

	@Override
	public void setApprovalStatus(emApprovalStatus value) {
		this.getHost().setApprovalStatus(value);
	}

	@Override
	public ICriteria getCriteria() {
		return this.getHost().getCriteria();
	}

	@Override
	public boolean isDirty() {
		return this.getHost().isDirty();
	}

	@Override
	public boolean isDeleted() {
		return this.getHost().isDeleted();
	}

	@Override
	public boolean isNew() {
		return this.getHost().isNew();
	}

	@Override
	public boolean isSavable() {
		return this.getHost().isSavable();
	}

	@Override
	public boolean isLoading() {
		return this.getHost().isLoading();
	}

}

class BOInstanceLogContract extends BOContract<IBOStorageTag> implements IBOInstanceLogContract {

	public BOInstanceLogContract(IBOStorageTag host) {
		super(host);
	}

	@Override
	public String getObjectCode() {
		return this.getHost().getObjectCode();
	}

	@Override
	public DateTime getCreateDate() {
		return this.getHost().getCreateDate();
	}

	@Override
	public void setCreateDate(DateTime value) {
		this.getHost().setCreateDate(value);

	}

	@Override
	public Short getCreateTime() {
		return this.getHost().getCreateTime();
	}

	@Override
	public void setCreateTime(Short value) {
		this.getHost().setCreateTime(value);

	}

	@Override
	public DateTime getUpdateDate() {
		return this.getHost().getUpdateDate();
	}

	@Override
	public void setUpdateDate(DateTime value) {
		this.getHost().setUpdateDate(value);

	}

	@Override
	public Short getUpdateTime() {
		return this.getHost().getUpdateTime();
	}

	@Override
	public void setUpdateTime(Short value) {
		this.getHost().setUpdateTime(value);

	}

	@Override
	public Integer getLogInst() {
		return this.getHost().getLogInst();
	}

	@Override
	public void setLogInst(Integer value) {
		this.getHost().setLogInst(value);
	}

	@Override
	public Integer getCreateUserSign() {
		return this.getHost().getCreateUserSign();
	}

	@Override
	public void setCreateUserSign(Integer value) {
		this.getHost().setCreateUserSign(value);

	}

	@Override
	public Integer getUpdateUserSign() {
		return this.getHost().getUpdateUserSign();
	}

	@Override
	public void setUpdateUserSign(Integer value) {
		this.getHost().setUpdateUserSign(value);
	}

	@Override
	public String getCreateActionId() {
		return this.getHost().getCreateActionId();
	}

	@Override
	public void setCreateActionId(String value) {
		this.getHost().setCreateActionId(value);
	}

	@Override
	public String getUpdateActionId() {
		return this.getHost().getUpdateActionId();
	}

	@Override
	public void setUpdateActionId(String value) {
		this.getHost().setUpdateActionId(value);
	}

	@Override
	public String getDataSource() {
		return this.getHost().getDataSource();
	}

	@Override
	public void setDataSource(String value) {
		this.getHost().setDataSource(value);
	}

}

class BORulesContract extends BOContract<IBusinessObject> implements IBORulesContract {

	public BORulesContract(IBusinessObject host) {
		super(host);
	}
}