package org.colorcoding.ibas.bobas.bo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.IBindableBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfoList;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 用户字段集合
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ UserField.class, UserFieldProxy.class })
@XmlType(name = "UserFields", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
class UserFields extends ArrayList<IUserField> implements IUserFields, IBindableBase {

	protected static final String MSG_REGISTER_USER_FIELD = "user fields: new user field [%s|%s].";

	private static final long serialVersionUID = -5629253015034083867L;

	public UserFields(BusinessObject<?> parent) {
		this.parent = parent;
		if (this.parent != null) {
			for (UserField item : UserFieldManager.create(this.parent.getClass())) {
				this.add(item);
			}
		}
	}

	private BusinessObject<?> parent;

	transient private PropertyChangeSupport listeners;

	@Override
	public final void registerListener(PropertyChangeListener listener) {
		if (this.listeners == null) {
			this.listeners = new PropertyChangeSupport(this);
		}
		this.listeners.addPropertyChangeListener(listener);
	}

	@Override
	public final void removeListener(PropertyChangeListener listener) {
		if (this.listeners == null) {
			this.listeners = new PropertyChangeSupport(this);
		}
		this.listeners.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String name, Object oldValue, Object newValue) {
		if (this.listeners == null) {
			return;
		}
		this.listeners.firePropertyChange(name, oldValue, newValue);
	}

	@Override
	public boolean add(IUserField e) {
		boolean done = super.add(e);
		if (done && (e instanceof IBindableBase)) {
			IBindableBase bindable = (IBindableBase) e;
			bindable.registerListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					UserFields.this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
				}
			});
		}
		return done;
	}

	@Override
	public IUserField get(String name) {
		for (IUserField item : this) {
			if (item.getName().equals(name)) {
				return item;
			}
		}
		throw new IndexOutOfBoundsException(I18N.prop("msg_bobas_user_field_not_exist", name));
	}

	public IFieldData[] getFields() {
		IFieldData[] fields = new IFieldData[this.size()];
		for (int i = 0; i < this.size(); i++) {
			UserField userField = (UserField) this.get(i);
			fields[i] = userField.getFieldData();
		}
		return fields;
	}

	public UserFieldProxy[] toProxies() {
		ArrayList<UserFieldProxy> proxyFields = new ArrayList<>(this.size());
		for (IUserField userField : this) {
			UserFieldProxy proxyField = new UserFieldProxy();
			proxyField.setName(userField.getName());
			proxyField.setValueType(userField.getValueType());
			proxyField.setValue(userField.getValue());
			proxyFields.add(proxyField);
		}
		proxyFields.sort(new Comparator<UserFieldProxy>() {
			@Override
			public int compare(UserFieldProxy o1, UserFieldProxy o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return proxyFields.toArray(new UserFieldProxy[] {});
	}

	/**
	 * 注册用户字段
	 * 
	 * @param name
	 *            名称
	 * @param valueType
	 *            值类型
	 */
	public IUserField register(String name, DbFieldType valueType) {
		UserField userField = UserFieldManager.create(name, valueType);
		if (userField != null) {
			Logger.log(MessageLevel.DEBUG, MSG_REGISTER_USER_FIELD, this.parent, name, valueType);
			this.add(userField);
		}
		return userField;
	}

	/**
	 * 注册到全局，新实例自动带出
	 */
	public void register() {
		if (this.size() == 0 || this.parent == null) {
			return;
		}
		PropertyInfoList propertyInfoList = new PropertyInfoList(this.size());
		for (IUserField item : this) {
			if (item instanceof UserField) {
				UserField userField = (UserField) item;
				propertyInfoList.add(new PropertyInfo<>(item.getName(), userField.getFieldData().getValueType()));
			}
		}
		propertyInfoList.sort(new Comparator<IPropertyInfo<?>>() {
			@Override
			public int compare(IPropertyInfo<?> o1, IPropertyInfo<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		UserFieldManager.register(this.parent.getClass(), propertyInfoList);
	}

}
