package org.colorcoding.ibas.bobas.bo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
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
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
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
import org.colorcoding.ibas.bobas.rule.BusinessRuleException;
import org.colorcoding.ibas.bobas.rule.BusinessRulesFactory;
import org.colorcoding.ibas.bobas.rule.IBusinessRule;
import org.colorcoding.ibas.bobas.rule.IBusinessRules;
import org.colorcoding.ibas.bobas.rule.IBusinessRulesManager;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;

/*
 * 业务对象基础类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObject", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public abstract class BusinessObject<T extends IBusinessObject> extends BusinessObjectBase<T>
		implements IBusinessObject {

	protected static final String MSG_USER_SET_FIELD_VALUE = "user fields: set field [%s]'s value [%s].";
	protected static final String MSG_RULES_EXECUTING_FAILD = "rules: field [%s] triggered rules fail to run, %s.";

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
			// 继承了自定义字段，初始化列表
			if (this.userFields == null) {
				this.userFields = new UserFields(this.getClass());
				this.userFields.registerListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						// 触发属性改变事件
						markDirty();
						firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getOldValue());
					}
				});
			}
		}
	}

	/**
	 * 获取自身查询条件
	 * 
	 * @return 自身的查询条件 ,新数据返回null
	 */
	@Override
	public ICriteria getCriteria() {
		if (this.isNew()) {
			return null;
		}
		Criteria criteria = new Criteria();
		if (this instanceof IBOStorageTag) {
			IBOStorageTag tagBO = (IBOStorageTag) this;
			criteria.setBusinessObject(tagBO.getObjectCode());
		}
		for (IFieldData item : this.getFields(c -> c.isPrimaryKey())) {
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(item.getName());
			condition.setValue(String.valueOf(item.getValue()));
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
		}
		if (boCode == null || boCode.isEmpty()) {
			return super.toString();
		}
		stringBuilder.append("{");
		stringBuilder.append("[");
		stringBuilder.append(boCode);
		stringBuilder.append("]");
		stringBuilder.append(".");
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
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		return this.getIdentifiers();
	}

	@Override
	public String toString(String type) {
		ISerializer<?> serializer = SerializerFactory.create().createManager().create(type);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(this, writer);
		return writer.toString();
	}

	/*
	 * 创建副本 可重载
	 * 
	 * @see club.ibas.bobas.businessobjectscommon.core.BusinessObjectBase#clone()
	 */
	@Override
	public T clone() {
		T nBO = (T) super.clone();
		if (nBO instanceof BusinessObject<?>) {
			BusinessObject<?> bo = (BusinessObject<?>) nBO;
			bo.resetStatus();
		}
		return nBO;
	}

	/**
	 * 重置对象状态
	 */
	protected void resetStatus() {
		this.markNew();
		// 重置对象存储标记
		if (this instanceof IBOStorageTag) {
			IBOStorageTag tagBO = (IBOStorageTag) this;
			tagBO.setLogInst(0);
			tagBO.setDataSource(null);
			tagBO.setCreateActionId(null);
			tagBO.setCreateDate(DateTime.minValue);
			tagBO.setCreateTime((short) 0);
			tagBO.setCreateUserSign(OrganizationFactory.UNKNOWN_USER.getId());
			tagBO.setUpdateActionId(null);
			tagBO.setUpdateDate(DateTime.minValue);
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
			docBO.setDocNum(0);
			docBO.setPeriod(0);
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
		// 重置子项状态
		for (IFieldData fieldData : this.getFields()) {
			if (fieldData.getValue() instanceof BusinessObject<?>) {
				// 业务对象
				BusinessObject<?> bo = (BusinessObject<?>) fieldData.getValue();
				bo.resetStatus();
			} else if (fieldData.getValue() instanceof BusinessObjects<?, ?>) {
				// 业务对象集合
				BusinessObjects<?, ?> bos = (BusinessObjects<?, ?>) fieldData.getValue();
				for (IBusinessObject item : bos) {
					if (item instanceof BusinessObject<?>) {
						BusinessObject<?> bo = (BusinessObject<?>) item;
						bo.resetStatus();
					}
				}
			}
		}
	}

	@Override
	public final IFieldData[] getFields() {
		IFieldData[] boFieldDatas = super.getFields();
		if (!(this instanceof IBOUserFields) || this.userFields == null) {
			return boFieldDatas;
		}
		// 处理自定义字段
		IFieldData[] allFieldDatas = new IFieldData[boFieldDatas.length + this.userFields.size()];
		int i = 0;
		for (IFieldData iFieldData : boFieldDatas) {
			allFieldDatas[i] = iFieldData;
			i++;
		}
		for (IFieldData iFieldData : this.userFields.getFieldDatas()) {
			allFieldDatas[i] = iFieldData;
			i++;
		}
		return allFieldDatas;
	}

	@Override
	public final IFieldData getField(String name) {
		// 重写方法加入自定义字段处理
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
	 * 用户自定义字段
	 * 
	 * @return 自定义字段集合
	 */
	public final UserFields getUserFields() {
		return this.userFields;
	}

	@XmlElementWrapper(name = "UserFields")
	@XmlElement(name = "UserField", type = UserFieldProxy.class, required = false)
	private UserFieldProxy[] getUserFieldProxys() {
		if (this.userFields == null) {
			return null;
		}
		return this.userFields.toProxyArray();
	}

	@SuppressWarnings("unused")
	private void setUserFieldProxys(UserFieldProxy[] value) {
		if (this.userFields == null || value == null) {
			return;
		}
		for (IUserField userField : value) {
			Logger.log(MessageLevel.DEBUG, MSG_USER_SET_FIELD_VALUE, userField.getName(), userField.getValue(),
					userField.getValueType());
			IUserField has = this.userFields.get(userField.getName());
			if (has != null) {
				has.setValue(((UserFieldProxy) userField).convertValue(has.getValueType()));
			}
		}
	}

	/**
	 * 循环属性，值为IBusinessObject执行方法
	 * 
	 * @param action
	 */
	protected void traverse(Consumer<IBusinessObject> action) {
		if (action == null) {
			return;
		}
		for (IFieldData item : this.getFields()) {
			Object data = item.getValue();
			if (data == null) {
				continue;
			}
			if (data instanceof IBusinessObject) {
				// 值是业务对象
				action.accept((IBusinessObject) data);
			} else if (data instanceof IBusinessObjects<?, ?>) {
				// 值是业务对象列表
				IBusinessObjects<?, ?> boList = (IBusinessObjects<?, ?>) data;
				for (IBusinessObject childItem : boList) {
					if (childItem instanceof IBusinessObject) {
						action.accept((IBusinessObject) childItem);
					}
				}
			} else if (data.getClass().isArray()) {
				// 值是数组
				int length = Array.getLength(data);
				for (int i = 0; i < length; i++) {
					Object childItem = Array.get(data, i);
					if (childItem instanceof IBusinessObject) {
						action.accept((IBusinessObject) childItem);
					}
				}
			}
		}
	}

	/**
	 * 标记为未修改
	 * 
	 * @param forced
	 *            包括子项及属性
	 */
	@Override
	public final void markOld(boolean forced) {
		super.markOld();
		if (forced) {
			this.traverse((data) -> {
				if (data instanceof ITrackStatusOperator) {
					((ITrackStatusOperator) data).markOld(true);
				}
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
	 * @param forced
	 *            包括子项及属性
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
		if (!this.isNew()) {
			// 非新建状态删除可用
			if (this instanceof IBOTagDeleted) {
				IBOTagDeleted tagBO = (IBOTagDeleted) this;
				if (tagBO.getReferenced() == emYesNo.YES) {
					// 被引用的数据，不允许删除
					tagBO.setDeleted(emYesNo.YES);
					return;
				}
			}
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
	private void initializeRules() throws RuntimeException {
		try {
			IBusinessRulesManager manager = BusinessRulesFactory.create().createManager();
			IBusinessRules rules = manager.getRules(this.getClass());
			if (rules != null && !rules.isInitialized()) {
				// 未初始化，则进行初始化
				rules.registerRules(this.registerRules());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设置属性值之后的回掉方法
	 */
	@Override
	protected <P> void afterSetProperty(IPropertyInfo<P> property) {
		// 触发业务逻辑运行
		if (!this.isLoading()) {
			// 读取数据时，不执行业务规则
			try {
				IBusinessRules rules = BusinessRulesFactory.create().createManager().getRules(this.getClass());
				if (rules != null) {
					rules.execute(this, property);
				}
			} catch (BusinessRuleException e) {
				// 运行中，仅记录错误，以被调试。
				Logger.log(MessageLevel.ERROR, MSG_RULES_EXECUTING_FAILD, property.getName(), e.getMessage());
			}
		}
	}
}
