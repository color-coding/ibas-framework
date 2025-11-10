package org.colorcoding.ibas.bobas.bo;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IBindable;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.rule.BusinessRulesManager;
import org.colorcoding.ibas.bobas.rule.IBusinessRule;
import org.colorcoding.ibas.bobas.rule.IBusinessRules;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerManager;

/**
 * 业务对象基础类型
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BusinessObject<T extends IBusinessObject> extends FieldedObject
		implements IBusinessObject, Serializable, IBindable {

	private static final long serialVersionUID = 1L;

	public BusinessObject() {
		super();
		this.setLoading(true);
		this.initializeRules();
		this.initialize();
		this.setLoading(false);
	}

	private boolean isValid = true;

	@Override
	public final boolean isValid() {
		return this.isValid;
	}

	public final void setValid(boolean value) {
		if (this.isValid == value) {
			return;
		}
		this.isValid = value;
		this.firePropertyChange("isValid", !this.isValid, this.isValid);
	}

	private boolean isBusy = false;

	@Override
	public final boolean isBusy() {
		return this.isBusy;
	}

	public final void setBusy(boolean value) {
		if (value == this.isBusy) {
			return;
		}
		this.isBusy = value;
		this.firePropertyChange("isBusy", this.isBusy, !this.isBusy);
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
					condition.setValue((Object) this.getProperty(property));
				}
			}
		} else {
			// 非新建状态，使用主键条件
			for (IPropertyInfo<?> property : this.properties().where(c -> c.isPrimaryKey())) {
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(property.getName());
				condition.setValue((Object) this.getProperty(property));
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
					if (tagBO.getDeleted() != emYesNo.YES) {
						tagBO.setDeleted(emYesNo.YES);
					}
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

	/**
	 * 不能被保存
	 */
	public void unsavable() {
		this.setSavable(false);
		BOUtilities.traverse(this, (data) -> {
			data.unsavable();
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public T clone() {
		try {
			// 使用默认管理员
			ISerializer serializer = new SerializerManager().create();
			T nData = (T) serializer.clone(this);
			if (nData instanceof BusinessObject) {
				((BusinessObject<T>) nData).reset();
			}
			return nData;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 重置对象状态
	 */
	public void reset() {
		Consumer<BusinessObject<?>> action = (data) -> {
			data.setLoading(true);
			data.setValid(true);
			data.setBusy(false);
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
			// 重置审批状态
			if (data instanceof IApprovalData) {
				IApprovalData apData = (IApprovalData) data;
				apData.setApprovalStatus(emApprovalStatus.UNAFFECTED);
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
			data.setLoading(false);
		};
		action.accept(this);
		BOUtilities.traverse(this, action);
	}

	/**
	 * 初始化业务规则
	 * 
	 * @throws RuntimeException
	 */
	private void initializeRules() {
		BusinessRulesManager manager = BusinessRulesManager.create();
		IBusinessRules rules = manager.getRules(this.getClass());
		if (rules != null && !rules.isInitialized()) {
			synchronized (rules) {
				// 未初始化，则进行初始化
				rules.register(this.registerRules());
			}
		}
	}

	/**
	 * 注册的业务规则
	 */
	protected IBusinessRule[] registerRules() {
		return null;
	}

	/**
	 * 属性（包含用户字段）
	 * 
	 * @return
	 */
	@Override
	public synchronized final List<IPropertyInfo<?>> properties() {
		if (this instanceof IBOUserFields && this.userFields != null && !this.userFields.isEmpty()) {
			List<IPropertyInfo<?>> propertyInfos = super.properties();
			propertyInfos.addAll(this.userFields.keySet());
			return propertyInfos;
		} else {
			return super.properties();
		}
	}

	/**
	 * 获取属性的值
	 * 
	 * @param property 依赖属性
	 * 
	 * @return 属性的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized final <P> P getProperty(IPropertyInfo<?> property) {
		Objects.requireNonNull(property);
		if (Strings.isWith(property.getName(), IBOUserFields.USER_FIELD_PREFIX_SIGN, null)) {
			if (this.userFields != null && this.userFields.containsKey(property)) {
				P value = (P) this.userFields.get(property);
				// 值是空，则使用默认值（减少内存占用）
				if (value == null) {
					return (P) property.getDefaultValue();
				}
				return value;
			}
			throw new IllegalArgumentException(
					Strings.format("[%s] not exists property [%s].", this.getClass().getName(), property.getName()));
		} else {
			return super.getProperty(property);
		}
	}

	/**
	 * 设置属性的值
	 * 
	 * @param property 依赖属性
	 * 
	 * @param value    新的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized final <P> void setProperty(IPropertyInfo<?> property, P value) {
		Objects.requireNonNull(property);
		if (Strings.isWith(property.getName(), IBOUserFields.USER_FIELD_PREFIX_SIGN, null)) {
			if (this.userFields != null) {
				if (!this.userFields.containsKey(property)) {
					throw new IllegalArgumentException(Strings.format("[%s] not exists property [%s].",
							this.getClass().getName(), property.getName()));
				}
				if (this.isLoading()) {
					this.userFields.put(property, value);
				} else {
					P oldValue = (P) this.userFields.get(property);
					if (oldValue != value) {
						this.userFields.put(property, value);
						if (this.modifiedProperties != null && !this.modifiedProperties.contains(property)) {
							this.modifiedProperties.add(property);
						}
						this.markDirty();
						this.firePropertyChange(property.getName(), oldValue, value);
					}
				}
			}
		} else {
			super.setProperty(property, value);
		}
	}

	public final IUserFields getUserFields() {
		if (this.userFields != null) {
			return new UserFields(this);
		}
		return UserFields.EMPTY_DATA;
	}

	@XmlElementWrapper(name = "UserFields")
	@XmlElement(name = "UserField", type = UserFieldProxy.class, required = false)
	private UserFieldProxy[] getUserFieldProxies() {
		if (this.userFields == null) {
			return null;
		}
		if (this.userFields.isEmpty()) {
			return null;
		}
		int i = 0;
		UserFieldProxy proxyField;
		UserFieldProxy[] proxyFields = new UserFieldProxy[this.userFields.size()];
		for (Entry<IPropertyInfo<?>, Object> entry : this.userFields.entrySet()) {
			proxyField = new UserFieldProxy();
			proxyField.setName(entry.getKey().getName());
			proxyField.setValueType(entry.getKey().getValueType());
			proxyField.setValue(entry.getValue());
			proxyFields[i] = proxyField;
			i += 1;
		}
		return proxyFields;
	}

	@SuppressWarnings("unused")
	private void setUserFieldProxies(UserFieldProxy[] values) {
		if (this.userFields == null || values == null) {
			return;
		}
		IPropertyInfo<?> propertyInfo;
		List<IPropertyInfo<?>> propertyInfos = new ArrayList<>(this.userFields.keySet());
		for (UserFieldProxy proxyField : values) {
			propertyInfo = propertyInfos
					.firstOrDefault(c -> Strings.equalsIgnoreCase(c.getName(), proxyField.getName()));
			if (propertyInfo == null) {
				continue;
			}
			this.userFields.put(propertyInfo, DataConvert.convert(propertyInfo.getValueType(), proxyField.getValue()));
		}
	}

	transient Map<IPropertyInfo<?>, Object> userFields = null;

	void firePropertyChange(IPropertyInfo<?> userField, Object oldValue, Object newValue) {
		if (this.isLoading()) {
			return;
		}
		if (this.modifiedProperties != null && !this.modifiedProperties.contains(userField)) {
			this.modifiedProperties.add(userField);
		}
		this.markDirty();
		super.firePropertyChange(userField.getName(), oldValue, newValue);
	}

	/**
	 * 初始化数据
	 */
	protected void initialize() {
		if (this instanceof IBOUserFields) {
			this.userFields = UserFieldsFactory.initFields(this.getClass());
		}
	}

}
