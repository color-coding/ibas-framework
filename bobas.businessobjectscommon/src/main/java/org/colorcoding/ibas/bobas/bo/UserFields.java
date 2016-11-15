package org.colorcoding.ibas.bobas.bo;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.core.BindableBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 自定义字段集合
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "UserFields", namespace = MyConsts.NAMESPACE_BOBAS_BO)
@XmlSeeAlso({ UserField.class, UserFieldProxy.class })
public class UserFields extends BindableBase implements Iterable<IUserField> {

	public UserFields() {
	}

	public UserFields(Class<?> boType) {
		this.setUserFields(UserFieldsFactory.create().create(boType));
		this.boType = boType;
	}

	private Class<?> boType = null;
	private UserField[] userFields = null;

	final UserField[] getUserFields() {
		return this.userFields;
	}

	final void setUserFields(UserField[] userFields) {
		this.userFields = userFields;
	}

	final UserFieldProxy[] toProxyArray() {
		if (this.getUserFields() == null || this.getUserFields().length == 0) {
			return null;
		}
		UserFieldProxy[] userFields = new UserFieldProxy[this.size()];
		int i = 0;
		for (IUserField userField : this.getUserFields()) {
			UserFieldProxy tmp = new UserFieldProxy();
			tmp.setName(userField.getName());
			tmp.setValueType(userField.getValueType());
			tmp.setValue(userField.getValue());
			userFields[i] = tmp;
			i++;
		}
		return userFields;
	}

	final IFieldData[] getFieldDatas() {
		IFieldData[] userFieldDatas = new IFieldData[this.size()];
		if (this.getUserFields() != null) {
			int i = 0;
			for (IUserField userField : this.getUserFields()) {
				if (userField instanceof UserField) {
					userFieldDatas[i] = ((UserField) userField).getFieldData();
				}
				i++;
			}
		}
		return userFieldDatas;
	}

	/**
	 * 添加用户定义字段
	 * 
	 * @param name
	 *            名称
	 * @param type
	 *            类型
	 */
	public void addUserField(String name, DbFieldType type) {
		UserField userField = UserFieldsFactory.create().create(name, type);
		if (userField != null) {
			ArrayList<UserField> tmpUserFields = new ArrayList<>();
			tmpUserFields.addAll(this.getUserFields());
			tmpUserFields.add(userField);
			this.setUserFields(tmpUserFields.toArray(new UserField[] {}));
		}
	}

	/**
	 * 注册已有用户字段（下次创建实例自动带出）
	 */
	public void register() {
		if (this.getUserFields() != null) {
			UserFieldInfo[] fieldInfos = new UserFieldInfo[this.size()];
			int index = 0;
			for (IUserField item : this.getUserFields()) {
				fieldInfos[index] = new UserFieldInfo(item.getName(), item.getValueType());
				index++;
			}
			UserFieldsFactory.create().register(this.boType, fieldInfos);
		}
	}

	public final int size() {
		return this.getUserFields() == null ? 0 : this.getUserFields().length;
	}

	@Override
	public final Iterator<IUserField> iterator() {
		return new Iterator<IUserField>() {
			private int nextSlot = 0;

			@Override
			public boolean hasNext() {
				if (getUserFields() == null || nextSlot >= getUserFields().length)
					return false;
				return true;
			}

			@Override
			public IUserField next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return getUserFields()[nextSlot++];
			}
		};
	}

	public IUserField get(String name) {
		if (this.getUserFields() != null) {
			for (UserField userField : this.userFields) {
				if (userField.getName().equals(name)) {
					return userField;
				}
			}
		}
		return null;
	}

	public IUserField get(int index) {
		if (this.getUserFields() != null) {
			if (index >= 0 && index < this.getUserFields().length) {
				return this.getUserFields()[index];
			}
		}
		return null;
	}

	public Object getValue(String name) {
		IUserField userField = this.get(name);
		if (userField != null) {
			return userField.getValue();
		} else {
			throw new UserFieldException(i18n.prop("msg_bobas_user_field_not_exist", name));
		}
	}

	public Object getValue(int index) {
		IUserField userField = this.get(index);
		if (userField != null) {
			return userField.getValue();
		} else {
			throw new UserFieldException(i18n.prop("msg_bobas_user_field_not_exist", index));
		}
	}

	public void setValue(String name, Object value) {
		IUserField userField = this.get(name);
		if (userField != null) {
			Object oldValue = userField.getValue();
			boolean changed = userField.setValue(value);
			if (changed) {
				this.firePropertyChange(userField.getName(), oldValue, userField.getValue());
			}
		} else {
			throw new UserFieldException(i18n.prop("msg_bobas_user_field_not_exist", name));
		}
	}

	public void setValue(int index, Object value) {
		IUserField userField = this.get(index);
		if (userField != null) {
			Object oldValue = userField.getValue();
			boolean changed = userField.setValue(value);
			if (changed) {
				this.firePropertyChange(userField.getName(), oldValue, userField.getValue());
			}
		} else {
			throw new UserFieldException(i18n.prop("msg_bobas_user_field_not_exist", index));
		}
	}
}
