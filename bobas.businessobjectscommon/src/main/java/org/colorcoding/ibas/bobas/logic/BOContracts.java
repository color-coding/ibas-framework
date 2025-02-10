package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOMaxValueKey;
import org.colorcoding.ibas.bobas.bo.IBOSeriesKey;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.logic.common.IBOApprovalContract;
import org.colorcoding.ibas.bobas.logic.common.IBOInstanceLogContract;
import org.colorcoding.ibas.bobas.logic.common.IBOKeysContract;
import org.colorcoding.ibas.bobas.logic.common.IBORulesContract;
import org.colorcoding.ibas.bobas.logic.common.IBOStorageTagContract;

/**
 * 业务对象契约（基类）
 * 
 * @param <T>
 */
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

/**
 * 对象主键契约
 */
class BOKeysContract extends BOContract<IBusinessObject> implements IBOKeysContract {

	public BOKeysContract(IBusinessObject host) {
		super(host);
	}

	@Override
	public String getObjectCode() {
		if (this.getHost() instanceof IBOLine) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getHost().getClass().getSimpleName());
			if (this.getHost() instanceof IBODocumentLine) {
				stringBuilder.append("-");
				stringBuilder.append(((IBODocumentLine) this.getHost()).getDocEntry());
			} else if (this.getHost() instanceof IBOSimpleLine) {
				stringBuilder.append("-");
				stringBuilder.append(((IBOSimpleLine) this.getHost()).getObjectKey());
			} else if (this.getHost() instanceof IBOMasterDataLine) {
				stringBuilder.append("-");
				stringBuilder.append(((IBOMasterDataLine) this.getHost()).getCode());
			}
			return stringBuilder.toString();
		} else {
			if (this.getHost() instanceof IBOStorageTag) {
				return ((IBOStorageTag) this.getHost()).getObjectCode();
			}
			BusinessObjectUnit boUnit = this.getHost().getClass().getAnnotation(BusinessObjectUnit.class);
			if (boUnit != null) {
				return boUnit.code();
			}
		}
		return Strings.VALUE_EMPTY;
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
	public Integer getSeries() {
		if (this.getHost() instanceof IBOSeriesKey) {
			return ((IBOSeriesKey) this.getHost()).getSeries();
		}
		return Numbers.INTEGER_VALUE_ZERO;
	}

	@Override
	public boolean setSeriesKey(String key) {
		if (this.getHost() instanceof IBOSeriesKey) {
			((IBOSeriesKey) this.getHost()).setSeriesValue(key);
			return true;
		}
		return false;
	}

	@Override
	public IPropertyInfo<?> getMaxValueField() {
		if (this.getHost() instanceof IBOMaxValueKey) {
			return ((IBOMaxValueKey) this.getHost()).getMaxValueField();
		}
		return null;
	}

	@Override
	public IPropertyInfo<?>[] getMaxValueConditions() {
		if (this.getHost() instanceof IBOMaxValueKey) {
			return ((IBOMaxValueKey) this.getHost()).getMaxValueConditions();
		}
		return new IPropertyInfo<?>[] {};
	}

	private String maxValueKey = null;

	@Override
	public String getMaxValueKey() {
		if (this.maxValueKey == null) {
			if (this.getHost() instanceof IBOMaxValueKey && this.getHost() instanceof BusinessObject<?>) {
				BusinessObject<?> bo = (BusinessObject<?>) this.getHost();
				StringBuilder stringBuilder = new StringBuilder();
				for (IPropertyInfo<?> item : this.getMaxValueConditions()) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append("&");
					}
					stringBuilder.append(item.getName());
					stringBuilder.append("=");
					stringBuilder.append(Strings.valueOf(bo.getProperty(item)));
				}
				this.maxValueKey = stringBuilder.toString();

			}
			this.maxValueKey = Strings.VALUE_EMPTY;
		}
		return this.maxValueKey;
	}

	@Override
	public boolean setMaxValue(Integer value) {
		if (this.getHost() instanceof IBOMaxValueKey) {
			return ((IBOMaxValueKey) this.getHost()).setMaxValue(value);
		} else if (this.getHost() instanceof IBOLine && this.getHost() instanceof BusinessObject<?>) {
			BusinessObject<?> bo = (BusinessObject<?>) this.getHost();
			bo.setProperty(this.getMaxValueField(), value);
			return true;
		}
		return false;
	}

	@Override
	public int getMaxValueStep() {
		if (this.getHost() instanceof IBOMaxValueKey) {
			return ((IBOMaxValueKey) this.getHost()).getMaxValueStep();
		}
		return 1;
	}
}

/**
 * 业务对象存储标记契约
 */
class BOStorageTagContract extends BOContract<IBOStorageTag> implements IBOStorageTagContract {

	public BOStorageTagContract(IBOStorageTag host) {
		super(host);
	}

}

/**
 * 业务对象审批契约
 */
class BOApprovalContract extends BOContract<IApprovalData> implements IBOApprovalContract {

	public BOApprovalContract(IApprovalData host) {
		super(host);
	}

}

/**
 * 业务对象实例日志契约
 */
class BOInstanceLogContract extends BOContract<IBOStorageTag> implements IBOInstanceLogContract {

	public BOInstanceLogContract(IBOStorageTag host) {
		super(host);
	}

}

/**
 * 业务对象规则契约
 */
class BORulesContract extends BOContract<IBusinessObject> implements IBORulesContract {

	public BORulesContract(IBusinessObject host) {
		super(host);
	}
}