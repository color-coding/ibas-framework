package org.colorcoding.ibas.bobas.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IFieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.xml.sax.InputSource;

/**
 * 半序列化（json字符串）
 */
class SerializerJson extends Serializer {

	private static final byte[][] CHAR_BYTES = new byte[128][];
	private static final int SIGN_INDEX_COLON = 127;

	static {
		CHAR_BYTES['"'] = "\\\"".getBytes();
		CHAR_BYTES['\\'] = "\\\\".getBytes();
		CHAR_BYTES['\b'] = "\\b".getBytes();
		CHAR_BYTES['\f'] = "\\f".getBytes();
		CHAR_BYTES['\n'] = "\\n".getBytes();
		CHAR_BYTES['\r'] = "\\r".getBytes();
		CHAR_BYTES['\t'] = "\\t".getBytes();
		CHAR_BYTES['/'] = "\\/".getBytes();

		for (int i = 0; i < 128; i++) {
			if (i < 32 || i > 126) {
				continue;
			}
			if (CHAR_BYTES[i] != null) {
				continue;
			}
			CHAR_BYTES[i] = Strings.alphabetOf(i).getBytes();
		}
		// 原始双引号
		CHAR_BYTES[SIGN_INDEX_COLON] = Strings.alphabetOf('"').getBytes();
	}

	protected void write(OutputStream outputStream, String value) throws IOException {
		if (value == null || value.isEmpty()) {
			return;
		}
		char cValue;
		int index = 0;
		for (int i = 0; i < value.length(); i++) {
			cValue = value.charAt(i);
			// 控制字符跳过
			if (cValue < 32 || cValue == 127) {
				continue;
			}
			if (cValue < 128) {
				if (index > 0) {
					outputStream.write(value.substring(index, i).getBytes("utf-8"));
					index = 0;
				}
				outputStream.write(CHAR_BYTES[cValue]);
			} else {
				index = i;
			}
		}
		if (index > 0) {
			outputStream.write(value.substring(index).getBytes("utf-8"));
		}
	}

	@Override
	protected void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES['{']);
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.write(outputStream, "type");
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		outputStream.write(CHAR_BYTES[':']);
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		XmlRootElement annotation = objectType.getAnnotation(XmlRootElement.class);
		if (annotation != null && !Strings.isNullOrEmpty(annotation.name())) {
			this.write(outputStream, annotation.name());
		} else {
			this.write(outputStream, objectType.getSimpleName());
		}
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.writeDelimiter(outputStream);
	}

	@Override
	protected void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES['}']);
	}

	@Override
	protected void writeDelimiter(OutputStream outputStream) throws IOException {
		outputStream.write(CHAR_BYTES[',']);
	}

	@Override
	protected void writePropertyValue(OutputStream outputStream, Object value) throws IOException {
		if (value instanceof String) {
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			this.write(outputStream, (String) value);
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		} else {
			this.write(outputStream, Strings.valueOf(value));
		}
	}

	@Override
	protected void writePropertyStart(OutputStream outputStream, IPropertyInfo<?> property, boolean isArray)
			throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.write(outputStream, property.getName());
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		outputStream.write(CHAR_BYTES[':']);
		if (isArray) {
			outputStream.write(CHAR_BYTES['[']);
		}
	}

	@Override
	protected void writePropertyEnd(OutputStream outputStream, IPropertyInfo<?> property, boolean isArray)
			throws IOException {
		if (isArray) {
			outputStream.write(CHAR_BYTES[']']);
		}
	}

}

/**
 * 半序列化（xml字符串）
 */
class SerializerXml extends Serializer {

	private static final byte[][] CHAR_BYTES = new byte[128][];

	private static final int SIGN_INDEX_LESS = 0;
	private static final int SIGN_INDEX_GREATER = 1;
	private static final int SIGN_INDEX_COLON = 127;

	static {
		CHAR_BYTES['&'] = "&amp;".getBytes();
		CHAR_BYTES['<'] = "&lt;".getBytes();
		CHAR_BYTES['>'] = "&gt;".getBytes();
		CHAR_BYTES['"'] = "&quot;".getBytes();
		CHAR_BYTES['\''] = "&apos;".getBytes();

		for (int i = 0; i < 128; i++) {
			if (CHAR_BYTES[i] != null) {
				continue;
			}
			if (i < 32 || i > 126) {
				continue;
			}
			CHAR_BYTES[i] = Strings.alphabetOf(i).getBytes();
		}
		// 原始小于号、大于号、冒号
		CHAR_BYTES[SIGN_INDEX_LESS] = Strings.alphabetOf('<').getBytes();
		CHAR_BYTES[SIGN_INDEX_GREATER] = Strings.alphabetOf('>').getBytes();
		CHAR_BYTES[SIGN_INDEX_COLON] = Strings.alphabetOf('"').getBytes();
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types) {
		try {
			// xml文件头
			outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
			this.write(outputStream, "?xml");
			outputStream.write(CHAR_BYTES[' ']);
			this.write(outputStream, "version=");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			this.write(outputStream, "1.0");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			outputStream.write(CHAR_BYTES[' ']);
			this.write(outputStream, "encoding=");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			this.write(outputStream, "UTF-8");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			outputStream.write(CHAR_BYTES[' ']);
			this.write(outputStream, "standalone=");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			this.write(outputStream, "yes");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			outputStream.write(CHAR_BYTES[' ']);
			this.write(outputStream, "?");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
		} catch (Exception e) {
			throw new SerializationException(e);
		}
		super.serialize(object, outputStream, formated, types);
	}

	protected void write(OutputStream outputStream, String value) throws IOException {
		if (value == null || value.isEmpty()) {
			return;
		}
		char cValue;
		int index = 0;
		for (int i = 0; i < value.length(); i++) {
			cValue = value.charAt(i);
			// 控制字符跳过
			if (cValue < 32 || cValue == 127) {
				continue;
			}
			if (cValue < 128) {
				if (index > 0) {
					outputStream.write(value.substring(index, i).getBytes("utf-8"));
					index = 0;
				}
				outputStream.write(CHAR_BYTES[cValue]);
			} else {
				index = i;
			}
		}
		if (index > 0) {
			outputStream.write(value.substring(index).getBytes("utf-8"));
		}
	}

	@Override
	protected void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		XmlRootElement annotation = objectType.getAnnotation(XmlRootElement.class);
		if (annotation != null && !Strings.isNullOrEmpty(annotation.namespace())) {
			this.write(outputStream, "ns2:");
		}
		if (annotation != null && !Strings.isNullOrEmpty(annotation.name())) {
			this.write(outputStream, annotation.name());
		} else {
			this.write(outputStream, objectType.getSimpleName());
		}
		if (annotation != null && !Strings.isNullOrEmpty(annotation.namespace())) {
			outputStream.write(CHAR_BYTES[' ']);
			this.write(outputStream, "xmlns:ns2=");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			this.write(outputStream, annotation.namespace());
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		}
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

	@Override
	protected void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		outputStream.write(CHAR_BYTES['/']);
		XmlRootElement annotation = objectType.getAnnotation(XmlRootElement.class);
		if (annotation != null && !Strings.isNullOrEmpty(annotation.namespace())) {
			this.write(outputStream, "ns2:");
		}
		this.write(outputStream, objectType.getSimpleName());
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

	@Override
	protected void writeDelimiter(OutputStream outputStream) throws IOException {

	}

	@Override
	protected void writePropertyStart(OutputStream outputStream, IPropertyInfo<?> property, boolean isArray)
			throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		this.write(outputStream, property.getName());
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

	@Override
	protected void writePropertyValue(OutputStream outputStream, Object value) throws IOException {
		this.write(outputStream, String.valueOf(value));
	}

	@Override
	protected void writePropertyEnd(OutputStream outputStream, IPropertyInfo<?> property, boolean isArray)
			throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		outputStream.write(CHAR_BYTES['/']);
		this.write(outputStream, property.getName());
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}
}

/**
 * 内部使用序列化
 */
abstract class Serializer implements ISerializer {

	@Override
	public <T> T clone(T object, Class<?>... types) throws SerializationException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, Class<?>... types) throws SerializationException {
		this.serialize(object, outputStream, false, types);
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types)
			throws SerializationException {
		if (!(object instanceof IFieldedObject)) {
			throw new SerializationException(I18N.prop("msg_bobas_invalid_argument", "object"));
		}
		this.serialize((IFieldedObject) object, outputStream, formated);
	}

	@Override
	public void schema(Class<?> type, OutputStream outputStream) throws SerializationException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	@Override
	public void validate(Class<?> type, String data) throws ValidateException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	@Override
	public <T> T deserialize(String data, Class<?>... types) throws SerializationException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	@Override
	public <T> T deserialize(InputStream inputStream, Class<?>... types) throws SerializationException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	@Override
	public <T> T deserialize(InputSource inputSource, Class<?>... types) throws SerializationException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	@Override
	public <T> T deserialize(Reader reader, Class<?>... types) throws SerializationException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method"));
	}

	protected void serialize(IFieldedObject object, OutputStream outputStream, boolean formated)
			throws SerializationException {
		if (object == null) {
			return;
		}
		List<IPropertyInfo<?>> properties = object.properties();
		if (properties.isEmpty()) {
			return;
		}
		try {
			IPropertyInfo<?> property;
			List<IPropertyInfo<?>> userProperties = new ArrayList<>();
			this.writeObjectStart(outputStream, object.getClass());
			int count = 0;
			for (int i = 0; i < properties.size(); i++) {
				property = properties.get(i);
				if (!(object instanceof UserField)) {
					if (Strings.startsWith(property.getName(), IBOUserFields.USER_FIELD_PREFIX_SIGN)) {
						userProperties.add(property);
						continue;
					}
				}
				if (property.getValueType() == String.class) {
					String value = object.getProperty(property);
					if (Strings.isNullOrEmpty(value)) {
						continue;
					}
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					this.writePropertyStart(outputStream, property, false);
					this.writePropertyValue(outputStream, value);
					this.writePropertyEnd(outputStream, property, false);
					count++;
				} else if (property.getValueType() == DateTime.class) {
					DateTime value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					if (DateTimes.VALUE_MIN == value) {
						continue;
					}
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					this.writePropertyStart(outputStream, property, false);
					this.writePropertyValue(outputStream, DateTimes.toString(value, DateTimes.FORMAT_DATE));
					this.writePropertyEnd(outputStream, property, false);
					count++;
				} else if (property.getValueType() == Boolean.class) {
					Boolean value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					this.writePropertyStart(outputStream, property, false);
					this.writePropertyValue(outputStream, value);
					this.writePropertyEnd(outputStream, property, false);
					count++;
				} else if (property.getValueType().isEnum()) {
					Enum<?> value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					this.writePropertyStart(outputStream, property, false);
					this.writePropertyValue(outputStream, value.name());
					this.writePropertyEnd(outputStream, property, false);
					count++;
				} else if (property.getValueType() == BigDecimal.class) {
					BigDecimal value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					if (Decimals.VALUE_ZERO == value) {
						continue;
					}
					if (BigDecimal.ZERO == value) {
						continue;
					}
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					if (value.compareTo(Decimals.VALUE_LONG_MAX_VALUE) > 0
							|| value.compareTo(Decimals.VALUE_LONG_MIN_VALUE) < 0) {
						this.writePropertyStart(outputStream, property, false);
						this.writePropertyValue(outputStream, Strings.valueOf(value));
						this.writePropertyEnd(outputStream, property, false);
					} else {
						this.writePropertyStart(outputStream, property, false);
						this.writePropertyValue(outputStream, value);
						this.writePropertyEnd(outputStream, property, false);
					}
					count++;
				} else if (property.getValueType() == BigInteger.class) {
					BigInteger value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					if (BigInteger.ZERO == value) {
						continue;
					}
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					if (value.compareTo(Decimals.VALUE_LONG_MAX_VALUE.toBigInteger()) > 0
							|| value.compareTo(Decimals.VALUE_LONG_MIN_VALUE.toBigInteger()) < 0) {
						this.writePropertyStart(outputStream, property, false);
						this.writePropertyValue(outputStream, Strings.valueOf(value));
						this.writePropertyEnd(outputStream, property, false);
					} else {
						this.writePropertyStart(outputStream, property, false);
						this.writePropertyValue(outputStream, value);
						this.writePropertyEnd(outputStream, property, false);
					}
					count++;
				} else if (property.getValueType() == Short.class || property.getValueType() == Integer.class
						|| property.getValueType() == Float.class || property.getValueType() == Double.class
						|| property.getValueType() == Long.class) {
					Object value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					if (Numbers.DOUBLE_VALUE_ZERO == value) {
						continue;
					}
					if (Numbers.FLOAT_VALUE_ZERO == value) {
						continue;
					}
					if (Numbers.SHORT_VALUE_ZERO == value) {
						continue;
					}
					if (Numbers.INTEGER_VALUE_ZERO == value) {
						continue;
					}
					if (Numbers.LONG_VALUE_ZERO == value) {
						continue;
					}
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					this.writePropertyStart(outputStream, property, false);
					this.writePropertyValue(outputStream, value);
					this.writePropertyEnd(outputStream, property, false);
					count++;
				} else if (java.util.List.class.isAssignableFrom(property.getValueType())) {
					List<?> list = object.getProperty(property);
					if (list != null && !list.isEmpty()) {
						if (i > 0) {
							this.writeDelimiter(outputStream);
						}
						this.writePropertyStart(outputStream, property, true);
						for (int j = 0; j < list.size(); j++) {
							if (j > 0) {
								this.writeDelimiter(outputStream);
							}
							this.serialize((IFieldedObject) list.get(j), outputStream, formated);
						}
						this.writePropertyEnd(outputStream, property, true);
						count++;
					}
					list = null;
				} else if (IFieldedObject.class.isAssignableFrom(property.getValueType())) {
					if (i > 0) {
						this.writeDelimiter(outputStream);
					}
					Object value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					this.writePropertyStart(outputStream, property, false);
					this.serialize((IFieldedObject) value, outputStream, formated);
					this.writePropertyEnd(outputStream, property, false);
					count++;
				} else {
					throw new SerializationException(
							I18N.prop("msg_bobas_data_type_not_support", property.getValueType()));
				}
			}
			if (!userProperties.isEmpty()) {
				Object value;
				UserField userField = new UserField();
				if (count > 0) {
					this.writeDelimiter(outputStream);
				}
				this.writePropertyStart(outputStream, UserField.PROPERTY_PARENT, true);
				for (int j = 0; j < userProperties.size(); j++) {
					property = userProperties.get(j);
					value = object.getProperty(property);
					if (value == null) {
						continue;
					}
					if (j > 0) {
						this.writeDelimiter(outputStream);
					}
					userField.setProperty(UserField.PROPERTY_NAME, property.getName());
					userField.setProperty(UserField.PROPERTY_VALUETYPE, property.getValueType().getName());
					userField.setPropertyValue(property, value);
					this.serialize(userField, outputStream, formated);
				}
				this.writePropertyEnd(outputStream, UserField.PROPERTY_PARENT, true);
			}
			this.writeObjectEnd(outputStream, object.getClass());
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

	protected abstract void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException;

	protected abstract void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException;

	protected abstract void writeDelimiter(OutputStream outputStream) throws IOException;

	protected abstract void writePropertyStart(OutputStream outputStream, IPropertyInfo<?> property, boolean isArray)
			throws IOException;

	protected abstract void writePropertyValue(OutputStream outputStream, Object value) throws IOException;

	protected abstract void writePropertyEnd(OutputStream outputStream, IPropertyInfo<?> property, boolean isArray)
			throws IOException;

}

class UserField extends FieldedObject {

	private static final long serialVersionUID = 1L;

	public static final IPropertyInfo<?> PROPERTY_PARENT = new IPropertyInfo<Object>() {

		@Override
		public String getName() {
			return "UserFields";
		}

		@Override
		public Class<?> getValueType() {
			return Object.class;
		}

		@Override
		public Object getDefaultValue() {
			return null;
		}

		@Override
		public int getIndex() {
			return 0;
		}

		@Override
		public boolean isPrimaryKey() {
			return false;
		}

		@Override
		public boolean isUniqueKey() {
			return false;
		}

		@Override
		public <A extends Annotation> A getAnnotation(Class<A> type) {
			return null;
		}
	};

	public static final IPropertyInfo<String> PROPERTY_NAME = registerProperty("Name", String.class, UserField.class);
	public static final IPropertyInfo<String> PROPERTY_VALUETYPE = registerProperty("ValueType", String.class,
			UserField.class);

	private IPropertyInfo<?> propertyValue;

	private Object value;

	public void setPropertyValue(IPropertyInfo<?> property, Object value) {
		this.propertyValue = property;
		this.value = value;
	}

	@Override
	public synchronized List<IPropertyInfo<?>> properties() {
		List<IPropertyInfo<?>> propertyInfos = super.properties();
		propertyInfos.add(new IPropertyInfo<Object>() {

			@Override
			public String getName() {
				return "Value";
			}

			@Override
			public Class<?> getValueType() {
				return UserField.this.propertyValue.getValueType();
			}

			@Override
			public Object getDefaultValue() {
				return null;
			}

			@Override
			public int getIndex() {
				return 1;
			}

			@Override
			public boolean isPrimaryKey() {
				return false;
			}

			@Override
			public boolean isUniqueKey() {
				return false;
			}

			@Override
			public <A extends Annotation> A getAnnotation(Class<A> type) {
				return null;
			}
		});
		return propertyInfos;
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized final <P> P getProperty(IPropertyInfo<?> property) {
		if (this.propertyValue == property) {
			return (P) this.value;
		} else if (property.getName().equals("Value")) {
			return (P) this.value;
		}
		return super.getProperty(property);
	}
}
