package org.colorcoding.ibas.bobas.bo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.BusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.period.IPeriodData;
import org.colorcoding.ibas.bobas.rule.BusinessRuleException;
import org.colorcoding.ibas.bobas.rule.BusinessRulesFactory;
import org.colorcoding.ibas.bobas.rule.IBusinessRule;
import org.colorcoding.ibas.bobas.rule.IBusinessRules;
import org.colorcoding.ibas.bobas.rule.IBusinessRulesManager;
import org.colorcoding.ibas.bobas.rule.ICheckRules;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;

/*
 * 业务对象基础类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObject", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public abstract class BusinessObject<T extends IBusinessObject> extends BusinessObjectBase<T>
		implements IBusinessObject {

	private static final long serialVersionUID = -8485128824221654376L;

	public BusinessObject() {
		super();
		this.initializeRules();
		this.setLoading(true);
		this.initialize();
		this.setLoading(false);
	}

	/**
	 * 初始化
	 */
	protected void initialize() {
		if (this instanceof IBOUserFields) {
			// 继承了用户字段，初始化列表
			if (this.userFields == null) {
				this.userFields = new UserFields(this);
				this.userFields.registerListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (BusinessObject.this.isLoading()) {
							return;
						}
						// 触发属性改变事件
						markDirty();
						firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
					}
				});
			}
		}
	}

	/**
	 * 获取自身查询条件（isNew使用唯一键，否则为主键）
	 * 
	 * @return 没有查询条件，返回null
	 */
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
				for (IFieldData item : this.getFields(c -> c.isUniqueKey())) {
					ICondition condition = criteria.getConditions().create();
					condition.setAlias(item.getName());
					condition.setValue(item.getValue());
				}
			}
		} else {
			// 非新建状态，使用主键条件
			for (IFieldData item : this.getFields(c -> c.isPrimaryKey())) {
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(item.getName());
				condition.setValue(item.getValue());
			}
		}
		if (criteria.getConditions().isEmpty()) {
			// 没有条件，返回空
			return null;
		}
		return criteria;
	}

	/**
	 * 获取识别码
	 * 
	 * @return
	 */
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
			for (IFieldData item : this.getFields(c -> c.isPrimaryKey())) {
				if (stringBuilder.length() > boCode.length() + 4) {
					stringBuilder.append("&");
				}
				stringBuilder.append("[");
				stringBuilder.append(item.getName());
				stringBuilder.append(" ");
				stringBuilder.append("=");
				stringBuilder.append(" ");
				stringBuilder.append(DataConvert.toString(item.getValue()));
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
	public String toString(String type) {
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			ISerializer<?> serializer = SerializerFactory.create().createManager().create(type);
			serializer.serialize(this, writer);
			return writer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 克隆对象
	 */
	@Override
	public T clone() {
		T nBO = (T) super.clone();
		if (nBO instanceof BusinessObject<?>) {
			BusinessObject<?> bo = (BusinessObject<?>) nBO;
			bo.reset();
		}
		return nBO;
	}

	/**
	 * 重置对象状态
	 */
	public void reset() {
		this.setLoading(true);
		this.markNew();
		// 重置对象存储标记
		if (this instanceof IBOStorageTag) {
			IBOStorageTag tagBO = (IBOStorageTag) this;
			tagBO.setLogInst(0);
			tagBO.setDataSource(null);
			tagBO.setCreateActionId(null);
			tagBO.setCreateDate(DateTime.MIN_VALUE);
			tagBO.setCreateTime((short) 0);
			tagBO.setCreateUserSign(OrganizationFactory.UNKNOWN_USER.getId());
			tagBO.setUpdateActionId(null);
			tagBO.setUpdateDate(DateTime.MIN_VALUE);
			tagBO.setUpdateTime((short) 0);
			tagBO.setUpdateUserSign(OrganizationFactory.UNKNOWN_USER.getId());
		}
		// 重置引用状态
		if (this instanceof IBOTagReferenced) {
			IBOTagReferenced tagBO = (IBOTagReferenced) this;
			tagBO.setReferenced(emYesNo.NO);
		}
		// 重置删除状态
		if (this instanceof IBOTagDeleted) {
			IBOTagDeleted tagBO = (IBOTagDeleted) this;
			tagBO.setDeleted(emYesNo.NO);
		}
		// 重置取消状态
		if (this instanceof IBOTagCanceled) {
			IBOTagCanceled tagBO = (IBOTagCanceled) this;
			tagBO.setCanceled(emYesNo.NO);
		}
		// 重置对象状态
		if (this instanceof IBODocument) {
			IBODocument docBO = (IBODocument) this;
			docBO.setStatus(emBOStatus.OPEN);
			docBO.setDocumentStatus(emDocumentStatus.PLANNED);
		}
		if (this instanceof IBODocumentLine) {
			IBODocumentLine docLineBO = (IBODocumentLine) this;
			docLineBO.setStatus(emBOStatus.OPEN);
			docLineBO.setLineStatus(emDocumentStatus.PLANNED);
		}
		// 重置审批状态
		if (this instanceof IApprovalData) {
			IApprovalData apData = (IApprovalData) this;
			apData.setApprovalStatus(emApprovalStatus.UNAFFECTED);
		}
		// 重置期间
		if (this instanceof IPeriodData) {
			IPeriodData pdData = (IPeriodData) this;
			pdData.setPeriod(0);
		}
		// 重置子项状态
		for (IFieldData fieldData : this.getFields()) {
			if (fieldData.getValue() instanceof BusinessObject<?>) {
				// 业务对象
				BusinessObject<?> bo = (BusinessObject<?>) fieldData.getValue();
				bo.reset();
			} else if (fieldData.getValue() instanceof BusinessObjects<?, ?>) {
				// 业务对象集合
				BusinessObjects<?, ?> bos = (BusinessObjects<?, ?>) fieldData.getValue();
				for (IBusinessObject item : bos) {
					if (item instanceof BusinessObject<?>) {
						BusinessObject<?> bo = (BusinessObject<?>) item;
						bo.reset();
					}
				}
			}
		}
		this.setLoading(false);
	}

	@Override
	public final IFieldData[] getFields() {
		IFieldData[] boFieldDatas = super.getFields();
		if (!(this instanceof IBOUserFields) || this.userFields == null) {
			return boFieldDatas;
		}
		// 处理用户字段
		IFieldData[] allFieldDatas = new IFieldData[boFieldDatas.length + this.userFields.size()];
		System.arraycopy(boFieldDatas, 0, allFieldDatas, 0, boFieldDatas.length);
		System.arraycopy(this.userFields.getFields(), 0, allFieldDatas, boFieldDatas.length,
				this.userFields.getFields().length);
		return allFieldDatas;
	}

	@Override
	public final IFieldData getField(String name) {
		// 重写方法加入用户字段处理
		if (name == null) {
			return null;
		}
		if (name.startsWith(UserField.USER_FIELD_PREFIX_SIGN) && (this instanceof IBOUserFields)
				&& this.userFields != null) {
			for (IUserField userField : userFields) {
				if (userField.getName().equals(name) && userField instanceof UserField) {
					return ((UserField) userField).getFieldData();
				}
			}
		} else {
			return super.getField(name);
		}
		return null;
	}

	private transient UserFields userFields = null;

	/**
	 * 用户字段
	 * 
	 * @return 用户字段集合
	 */
	public final IUserFields getUserFields() {
		return this.userFields;
	}

	@XmlElementWrapper(name = "UserFields")
	@XmlElement(name = "UserField", type = UserFieldProxy.class, required = false)
	private UserFieldProxy[] getUserFieldProxies() {
		if (this.userFields == null) {
			return null;
		}
		return this.userFields.toProxies();
	}

	@SuppressWarnings("unused")
	private void setUserFieldProxies(UserFieldProxy[] values) {
		if (this.userFields == null || values == null) {
			return;
		}
		for (UserFieldProxy proxyField : values) {
			IUserField userField = this.userFields.firstOrDefault(c -> c.getName().equals(proxyField.getName()));
			if (userField == null) {
				userField = this.userFields.register(proxyField.getName(), proxyField.getValueType());
			}
			if (userField == null) {
				continue;
			}
			userField.setValue(proxyField.convertValue());
		}
	}

	/**
	 * 循环属性，值为IBusinessObject执行方法
	 * 
	 * @param action
	 */
	protected void traverse(Consumer<BusinessObject<?>> action) {
		if (action == null) {
			return;
		}
		for (IFieldData item : this.getFields()) {
			Object data = item.getValue();
			if (data == null) {
				continue;
			}
			if (data instanceof BusinessObject<?>) {
				// 值是业务对象
				action.accept((BusinessObject<?>) data);
			} else if (data instanceof Iterable<?>) {
				// 值是业务对象列表
				Iterable<?> datas = (Iterable<?>) data;
				for (Object itemData : datas) {
					if (itemData instanceof BusinessObject<?>) {
						action.accept((BusinessObject<?>) itemData);
					}
				}
			} else if (data.getClass().isArray()) {
				// 值是数组
				int length = Array.getLength(data);
				for (int i = 0; i < length; i++) {
					Object itemData = Array.get(data, i);
					if (itemData instanceof BusinessObject<?>) {
						action.accept((BusinessObject<?>) itemData);
					}
				}
			}
		}
	}

	/**
	 * 标记为未修改
	 * 
	 * @param recursive 包括子项及属性
	 */
	@Override
	public final void markOld(boolean recursive) {
		super.markOld();
		if (recursive) {
			this.traverse((data) -> {
				data.markOld(recursive);
			});
		}
	}

	/**
	 * 不能被保存，不影响子项此状态
	 */
	public void unsavable() {
		super.setSavable(false);
	}

	/**
	 * 取消删除的数据
	 * 
	 */
	public final void undelete() {
		super.clearDeleted();
		this.traverse((data) -> {
			data.undelete();
		});
	}

	/**
	 * 删除数据
	 */
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
		this.traverse((data) -> {
			data.delete();
		});
	}

	/**
	 * 注册的业务规则
	 */
	protected IBusinessRule[] registerRules() {
		return null;
	}

	/**
	 * 初始化业务规则
	 * 
	 * @throws RuntimeException
	 */
	private void initializeRules() {
		IBusinessRulesManager manager = BusinessRulesFactory.create().createManager();
		IBusinessRules rules = manager.getRules(this.getClass());
		if (rules != null && !rules.isInitialized()) {
			synchronized (rules) {
				// 未初始化，则进行初始化
				rules.register(this.registerRules());
			}
		}
	}

	private volatile IBusinessRules myRules = null;

	/**
	 * 运行业务规则
	 * 
	 * @param properties 触发的属性
	 * @throws BusinessRuleException
	 */
	public void executeRules(IPropertyInfo<?>... properties) throws BusinessRuleException {
		if (this.isLoading()) {
			// 读取数据时，不执行业务规则
			return;
		}
		this.traverse((data) -> {
			data.executeRules(properties);
		});
		if (this.myRules == null) {
			this.myRules = BusinessRulesFactory.create().createManager().getRules(this.getClass());
		}
		if (this.myRules != null) {
			this.myRules.execute(this, properties);
		}
		if (this instanceof ICheckRules) {
			((ICheckRules) this).check();
		}
	}

	/**
	 * 设置属性值之后的回掉方法
	 */
	@Override
	protected <P> void afterSetProperty(IPropertyInfo<P> property) {
		if (!MyConfiguration.isLiveRules()) {
			return;
		}
		try {
			this.executeRules(property);
		} catch (BusinessRuleException e) {
			// 运行中，仅记录错误，以被调试。
			Logger.log(MessageLevel.DEBUG, e);
		}
	}
}
