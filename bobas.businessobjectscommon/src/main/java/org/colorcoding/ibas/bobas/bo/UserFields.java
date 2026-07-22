package org.colorcoding.ibas.bobas.bo;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

class UserFields implements IUserFields {

	public static final String ELEMENT_NAME = "UserField";

	public static final String WRAPPER_NAME = "UserFields";

	public static final IUserFields EMPTY_DATA = new UserFields(null);

	public UserFields(BusinessObject<?> bizObject) {
		this.parent = bizObject;
		this.initialize();
	}

	private BusinessObject<?> parent;
	private IUserField<?>[] fields;

	protected void initialize() {
		if (this.parent == null) {
			this.fields = new IUserField<?>[0];
			return;
		}
		synchronized (this.parent) {
			if (this.parent.userFields == null) {
				this.fields = new IUserField<?>[0];
				return;
			}
			int index = 0;
			this.fields = new IUserField<?>[this.parent.userFields.size()];
			for (IPropertyInfo<?> property : this.parent.userFields.keySet()) {
				this.fields[index] = new IUserField<Object>() {

					@Override
					public String getName() {
						return property.getName();
					}

					@Override
					public Class<?> getValueType() {
						return property.getValueType();
					}

					@Override
					public Object getValue() {
						synchronized (UserFields.this.parent) {
							return UserFields.this.parent.userFields.get(property);
						}
					}

					@Override
					public void setValue(Object value) {
						synchronized (UserFields.this.parent) {
							if (UserFields.this.parent.userFields.containsKey(property)) {
								Object oldValue = UserFields.this.parent.userFields.get(property);
								if (oldValue == null) {
									oldValue = property.getDefaultValue();
								}
								if (oldValue == null || value == null || !oldValue.equals(value)) {
									UserFields.this.parent.userFields.put(property, value);
									UserFields.this.parent.firePropertyChange(property, oldValue, value);
								}
							}
						}
					}

					@Override
					public final String toString() {
						return String.format("{userField: %s = %s}", this.getName(), this.getValue());
					}

				};
				index++;
			}
		}
	}

	@Override
	public int size() {
		return this.fields.length;
	}

	@Override
	public int indexOf(IUserField<?> item) {
		for (int i = 0; i < this.fields.length; i++) {
			if (this.fields[i] == item) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int indexOf(String name) {
		for (int i = 0; i < this.fields.length; i++) {
			if (this.fields[i].getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Iterator<IUserField<?>> iterator() {
		return new Iterator<IUserField<?>>() {

			int index = 0;

			@Override
			public IUserField<?> next() {
				if (!hasNext()) {
					throw new IllegalStateException("no more elements in user fields iterator");
				}
				return UserFields.this.fields[this.index++];
			}

			@Override
			public boolean hasNext() {
				if (UserFields.this.fields == null || index >= UserFields.this.fields.length)
					return false;
				return true;
			}
		};
	}

	/**
	 * 按名称获取用户字段；名称不存在时抛出IllegalArgumentException
	 *
	 * @param name 字段名称（忽略大小写）
	 * @return 用户字段
	 * @throws IllegalArgumentException 名称不存在时
	 */
	@Override
	public <P> IUserField<P> get(String name) {
		for (int i = 0; i < this.fields.length; i++) {
			if (this.fields[i].getName().equalsIgnoreCase(name)) {
				return this.get(i);
			}
		}
		throw new IllegalArgumentException(Strings.format("user field [%s] not found.", name));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> IUserField<P> get(int index) {
		return (IUserField<P>) this.fields[index];
	}

	/**
	 * 注册用户字段；若名称不存在则自动注册并重新初始化
	 *
	 * @param name      字段名称
	 * @param valueType 值类型
	 * @return 用户字段
	 */
	@Override
	public <P> IUserField<P> register(String name, Class<?> valueType) {
		if (this.parent == null) {
			throw new IllegalStateException("cannot register user field on empty user fields");
		}
		synchronized (this.parent) {
			if (this.indexOf(name) < 0) {
				IPropertyInfo<?> uFieldInfo = UserFieldsFactory.createManager().registerUserField(this.parent.getClass(),
						name, valueType);
				this.parent.userFields.put(uFieldInfo, null);
				this.initialize();
			}
			return this.get(name);
		}
	}

	@Override
	public String toString() {
		return String.format("{userFields: [%s]}", this.fields.length);
	}
}
