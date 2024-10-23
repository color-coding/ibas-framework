package org.colorcoding.ibas.bobas.bo;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.IArrayList;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 业务对象基础类型
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BusinessObject<T extends IBusinessObject> extends FieldedObject implements IBusinessObject {

	private static final long serialVersionUID = 1L;

	public BusinessObject() {
		super();
		this.setLoading(true);
		if (this instanceof IBOUserFields) {
			this.userFields = UserFieldsManager.initFields(this.getClass());
		}
		this.setLoading(false);
	}

	@Override
	public ICriteria getCriteria() {
		Criteria criteria = new Criteria();
		if (this instanceof IBOStorageTag) {
			IBOStorageTag tagBO = (IBOStorageTag) this;
			criteria.setBusinessObject(tagBO.getObjectCode());
		}
		if (this.isNew()) {
			// 新建状态，使用唯一索引条件（主数据除外）
			if (this instanceof IBOMasterData) {
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOMasterData.MASTER_PRIMARY_KEY_NAME);
				condition.setValue(((IBOMasterData) this).getCode());
			} else if (this instanceof IBOMasterDataLine) {
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME);
				condition.setValue(((IBOMasterDataLine) this).getCode());
				condition = criteria.getConditions().create();
				condition.setAlias(IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME);
				condition.setValue(((IBOMasterDataLine) this).getLineId());
			} else {
				for (IPropertyInfo<?> property : this.properties().where(c -> c.isUniqueKey())) {
					ICondition condition = criteria.getConditions().create();
					condition.setAlias(property.getName());
					condition.setValue(this.getProperty(property));
				}
			}
		} else {
			// 非新建状态，使用主键条件
			for (IPropertyInfo<?> property : this.properties().where(c -> c.isPrimaryKey())) {
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(property.getName());
				condition.setValue(this.getProperty(property));
			}
		}
		if (criteria.getConditions().isEmpty()) {
			// 没有条件，返回空
			return null;
		}
		return criteria;
	}

	@Override
	public String getIdentifiers() {
		String boCode = null;
		StringBuilder stringBuilder = new StringBuilder();
		if (this instanceof IBOStorageTag) {
			IBOStorageTag tagBO = (IBOStorageTag) this;
			boCode = tagBO.getObjectCode();
		} else {
			boCode = this.getClass().getSimpleName();
		}
		if (boCode == null || boCode.isEmpty()) {
			return super.toString();
		}
		stringBuilder.append("{");
		stringBuilder.append("[");
		stringBuilder.append(boCode);
		stringBuilder.append("]");
		stringBuilder.append(".");
		if (this instanceof IBOMasterData) {
			stringBuilder.append("[");
			stringBuilder.append(IBOMasterData.MASTER_PRIMARY_KEY_NAME);
			stringBuilder.append(" ");
			stringBuilder.append("=");
			stringBuilder.append(" ");
			stringBuilder.append(((IBOMasterData) this).getCode());
			stringBuilder.append("]");
		} else {
			for (IPropertyInfo<?> property : this.properties().where(c -> c.isPrimaryKey())) {
				if (stringBuilder.length() > boCode.length() + 4) {
					stringBuilder.append("&");
				}
				stringBuilder.append("[");
				stringBuilder.append(property.getName());
				stringBuilder.append(" ");
				stringBuilder.append("=");
				stringBuilder.append(" ");
				stringBuilder.append(Strings.valueOf(this.getProperty(property)));
				stringBuilder.append("]");
			}
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		return this.getIdentifiers();
	}

	@Override
	public void delete() {
		boolean done = true;
		if (!this.isNew()) {
			// 非新建状态删除可用
			if (this instanceof IBOTagDeleted) {
				IBOTagDeleted tagBO = (IBOTagDeleted) this;
				if (tagBO.getReferenced() == emYesNo.YES) {
					// 被引用的数据，不允许删除
					tagBO.setDeleted(emYesNo.YES);
					done = false;
				}
			}
		}
		if (done) {
			this.markDeleted();
		}
		BOUtilities.traverse(this, (data) -> {
			data.delete();
		});
	}

	@Override
	public void undelete() {
		super.clearDeleted();
		BOUtilities.traverse(this, (data) -> {
			data.undelete();
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public T clone() {
		try {
			T nBO = (T) super.clone();
			if (nBO instanceof BusinessObject<?>) {
				((BusinessObject<?>) nBO).reset();
			}
			return nBO;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 重置对象状态
	 */
	public void reset() {
		BOUtilities.traverse(this, (data) -> {
			data.setLoading(true);
			data.markNew();
			// 重置对象存储标记
			if (data instanceof IBOStorageTag) {
				IBOStorageTag tagBO = (IBOStorageTag) data;
				tagBO.setLogInst(0);
				tagBO.setDataSource(null);
				tagBO.setCreateActionId(null);
				tagBO.setCreateDate(null);
				tagBO.setCreateTime(null);
				tagBO.setCreateUserSign(null);
				tagBO.setUpdateActionId(null);
				tagBO.setUpdateDate(null);
				tagBO.setUpdateTime(null);
				tagBO.setUpdateUserSign(null);
			}
			// 重置引用状态
			if (data instanceof IBOTagReferenced) {
				IBOTagReferenced tagBO = (IBOTagReferenced) data;
				tagBO.setReferenced(emYesNo.NO);
			}
			// 重置删除状态
			if (data instanceof IBOTagDeleted) {
				IBOTagDeleted tagBO = (IBOTagDeleted) data;
				tagBO.setDeleted(emYesNo.NO);
			}
			// 重置取消状态
			if (data instanceof IBOTagCanceled) {
				IBOTagCanceled tagBO = (IBOTagCanceled) data;
				tagBO.setCanceled(emYesNo.NO);
			}
			// 重置对象状态
			if (data instanceof IBODocument) {
				IBODocument docBO = (IBODocument) data;
				docBO.setStatus(emBOStatus.OPEN);
				docBO.setDocumentStatus(emDocumentStatus.PLANNED);
			}
			if (data instanceof IBODocumentLine) {
				IBODocumentLine docLineBO = (IBODocumentLine) data;
				docLineBO.setStatus(emBOStatus.OPEN);
				docLineBO.setLineStatus(emDocumentStatus.PLANNED);
			}
			// 重置审批状态
			if (data instanceof IApprovalData) {
				IApprovalData apData = (IApprovalData) data;
				apData.setApprovalStatus(emApprovalStatus.UNAFFECTED);
			}
			// 重置期间
			if (data instanceof IPeriodData) {
				IPeriodData pdData = (IPeriodData) data;
				pdData.setPeriod(0);
			}
			data.setLoading(false);
		});
	}

	/**
	 * 属性（包含用户字段）
	 * 
	 * @return
	 */
	public final IArrayList<IPropertyInfo<?>> properties() {
		if (this instanceof IBOUserFields && this.userFields != null && !this.userFields.isEmpty()) {
			IArrayList<IPropertyInfo<?>> propertyInfos = super.properties();
			for (IPropertyInfo<?> item : this.userFields.keySet()) {
				propertyInfos.add(item);
			}
			return propertyInfos;
		} else {
			return super.properties();
		}
	}

	public IUserFields getUserFields() {
		if (this.userFields != null) {
			return new UserFields(this);
		}
		return UserFields.EMPTY_DATA;
	}

	transient Map<IPropertyInfo<?>, Object> userFields = null;

	void firePropertyChange(IPropertyInfo<?> userField, Object oldValue, Object newValue) {
		if (this.isLoading()) {
			return;
		}
		if (this.modifiedFields != null && !this.modifiedFields.contains(userField)) {
			this.modifiedFields.add(userField);
		}
		this.markDirty();
		super.firePropertyChange(userField.getName(), oldValue, newValue);
	}

}
