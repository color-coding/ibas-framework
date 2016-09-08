package org.colorcoding.ibas.bobas.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.BusinessObjectBase;
import org.colorcoding.ibas.bobas.core.Serializer;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.UnknownUser;
import org.colorcoding.ibas.bobas.util.StringBuilder;

/*
 * 业务对象基础类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObject", namespace = MyConsts.NAMESPACE_BOBAS_BO)
public abstract class BusinessObject<T extends IBusinessObject> extends BusinessObjectBase<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8485128824221654376L;

	public BusinessObject() {
		super();
		this.initialize();
	}

	/**
	 * 初始化
	 */
	protected void initialize() {
		if (this instanceof IBOUserFields) {
			// 继承了自定义字段，初始化列表
			if (this.userFields == null) {
				this.userFields = new UserFields(this.getClass());
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
			criteria.setBusinessObjectCode(tagBO.getObjectCode());
		}
		for (IFieldData item : this.getKeyFields()) {
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(item.getName());
			condition.setCondVal(String.valueOf(item.getValue()));
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
		stringBuilder.appendFormat("{");
		stringBuilder.append("[");
		stringBuilder.append(boCode);
		stringBuilder.append("].");
		for (IFieldData item : this.getKeyFields()) {
			if (stringBuilder.length() > boCode.length() + 4) {
				stringBuilder.append("&");
			}
			stringBuilder.append("[");
			stringBuilder.append(item.getName());
			stringBuilder.append(" = ");
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
		return Serializer.toString(type, this, false);
	}

	/*
	 * 创建副本 可重载
	 * 
	 * @see
	 * club.ibas.bobas.businessobjectscommon.core.BusinessObjectBase#clone()
	 */
	@Override
	public T clone() {
		@SuppressWarnings("unchecked")
		T nBO = (T) Serializer.Clone(this);
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
			tagBO.setCreateActionId(null);
			tagBO.setCreateDate(DateTime.getMinValue());
			tagBO.setCreateTime((short) 0);
			tagBO.setCreateUserSign(UnknownUser.UNKNOWN_USER_SIGN);
			tagBO.setUpdateActionId(null);
			tagBO.setUpdateDate(DateTime.getMinValue());
			tagBO.setUpdateTime((short) 0);
			tagBO.setUpdateUserSign(UnknownUser.UNKNOWN_USER_SIGN);
		}
		// 重置引用状态
		if (this instanceof IBOReferenced) {
			IBOReferenced refBO = (IBOReferenced) this;
			refBO.setDeleted(emYesNo.No);
			refBO.setReferenced(emYesNo.No);
		}
		// 重置对象状态
		if (this instanceof IBODocument) {
			IBODocument docBO = (IBODocument) this;
			docBO.setCanceled(emYesNo.No);
			docBO.setDocumentStatus(emDocumentStatus.Planned);
			docBO.setStatus(emBOStatus.Open);
			docBO.setDocNum(0);
			docBO.setPeriod(0);
		} else if (this instanceof IBODocumentLine) {
			IBODocumentLine docLineBO = (IBODocumentLine) this;
			docLineBO.setCanceled(emYesNo.No);
			docLineBO.setStatus(emBOStatus.Open);
			docLineBO.setLineStatus(emDocumentStatus.Planned);
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
	public UserFields getUserFields() {
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
			RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_USER_SET_FIELD_VALUE, userField.getName(),
					userField.getValue(), userField.getValueType());
			IUserField has = this.userFields.get(userField.getName());
			if (has != null) {
				has.setValue(((UserFieldProxy) userField).convertValue(has.getValueType()));
			}
		}
	}

	/**
	 * 删除数据
	 */
	public void delete() {
		if (!this.isNew()) {
			// 非新建状态删除可用
			if (this instanceof IBOReferenced) {
				IBOReferenced refBO = (IBOReferenced) this;
				if (refBO.getReferenced() == emYesNo.Yes) {
					// 被引用的数据，不允许删除
					refBO.setDeleted(emYesNo.Yes);
					return;
				}
			}
			this.markDeleted(true);
		}
	}
}
