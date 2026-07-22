package org.colorcoding.ibas.bobas.bo;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IFieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.writer.CsvWriter;
import org.colorcoding.ibas.bobas.serialization.writer.JsonWriter;
import org.colorcoding.ibas.bobas.serialization.writer.Writer;
import org.colorcoding.ibas.bobas.serialization.writer.XmlWriter;

/**
 * 业务对象JSON序列化器（仅序列化，不支持反序列化）
 */
class SerializerJson extends Serializer {

	public final static String TYPE_SIGN = SerializationFactory.TYPE_JSON;

	@Override
	protected Writer createWriter() {
		return new JsonWriter();
	}
}

/**
 * 业务对象XML序列化器（仅序列化，不支持反序列化）
 */
class SerializerXml extends Serializer {

	public final static String TYPE_SIGN = SerializationFactory.TYPE_XML;

	@Override
	protected Writer createWriter() {
		return new XmlWriter();
	}
}

/**
 * 业务对象CSV序列化器（仅序列化，不支持反序列化；仅支持值类型属性）
 */
class SerializerCsv extends Serializer {

	public final static String TYPE_SIGN = "csv";

	@Override
	protected Writer createWriter() {
		return new CsvWriter();
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types)
			 {
		if (!(object instanceof IFieldedObject)) {
			throw new SerializationException(I18N.prop("msg_bobas_data_type_not_support",
					object != null ? object.getClass().getSimpleName() : "NULL"));
		}
		try {
			IFieldedObject fieldedObject = (IFieldedObject) object;
			List<IPropertyInfo<?>> properties = fieldedObject.properties();
			if (properties.isEmpty()) {
				return;
			}
			IPropertyInfo<?> property;
			Writer writer = this.createWriter();
			int count = 0;
			for (int i = 0; i < properties.size(); i++) {
				property = properties.get(i);
				if (!BOUtilities.isValueType(property)) {
					continue;
				}
				if (count > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.write(outputStream, property.getName());
				count++;
			}
			writer.writeNewLine(outputStream);
			count = 0;
			for (int i = 0; i < properties.size(); i++) {
				property = properties.get(i);
				if (!BOUtilities.isValueType(property)) {
					continue;
				}
				if (count > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.write(outputStream, Strings.valueOf(fieldedObject.getProperty(property)));
				count++;
			}
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	protected void serialize(IFieldedObject object, OutputStream outputStream, boolean formated) throws Exception {
		if (object instanceof UserField) {
			UserField userField = (UserField) object;
			this.getWriter().writePropertyValue(outputStream, userField.getPropertyValue());
		} else {
			super.serialize(object, outputStream, formated);
		}
	}
}

/**
 * 业务对象序列化基类（仅序列化，不支持反序列化/克隆/校验）
 */
abstract class Serializer implements ISerializer {

	protected abstract Writer createWriter();

	private Writer writer;

	public Writer getWriter() {
		if (this.writer == null) {
			this.writer = this.createWriter();
		}
		return writer;
	}

	@Override
	public <T> T clone(T object, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "clone"));
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, Class<?>... types)  {
		this.serialize(object, outputStream, false, types);
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types)
			 {
		if (!(object instanceof IFieldedObject)) {
			throw new SerializationException(I18N.prop("msg_bobas_data_type_not_support",
					object != null ? object.getClass().getSimpleName() : "NULL"));
		}
		try {
			this.getWriter().writeHeader(outputStream);
			this.serialize((IFieldedObject) object, outputStream, formated);
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	public void schema(Class<?> type, OutputStream outputStream)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "schema"));
	}

	@Override
	public void validate(Class<?> type, String data) throws ValidateException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "validate(String)"));
	}

	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "validate(InputStream)"));
	}

	@Override
	public <T> T deserialize(String data, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "deserialize(String)"));
	}

	@Override
	public <T> T deserialize(InputStream inputStream, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "deserialize(InputStream)"));
	}

	@Override
	public <T> T deserialize(Reader reader, Class<?>... types)  {
		throw new SerializationException(I18N.prop("msg_bobas_not_implemented_method", "deserialize(Reader)"));
	}

	protected void serialize(IFieldedObject object, OutputStream outputStream, boolean formated) throws Exception {
		if (object == null) {
			return;
		}
		List<IPropertyInfo<?>> properties = object.properties();
		if (properties.isEmpty()) {
			return;
		}
		Writer writer = this.createWriter();
		IPropertyInfo<?> property;
		List<IPropertyInfo<?>> userProperties = new ArrayList<>();

		writer.writeObjectStart(outputStream, object.getClass());
		int count = 0;
		for (int i = 0; i < properties.size(); i++) {
			property = properties.get(i);
			if (!(object instanceof UserField)) {
				if (Strings.startsWith(property.getName(), IBOUserFields.USER_FIELD_PREFIX_SIGN)
						&& !IPropertyInfo.class.getPackage().equals(property.getClass().getPackage())) {
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
					writer.writeDelimiter(outputStream);
				}
				writer.writePropertyStart(outputStream, property.getName(), false);
				writer.writePropertyValue(outputStream, value);
				writer.writePropertyEnd(outputStream, property.getName(), false);
				count++;
			} else if (property.getValueType() == DateTime.class) {
				DateTime value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				// 值等于VALUE_MIN即跳过（不依赖引用相同，兼容非缓存实例）
				if (value.getTime() == DateTimes.VALUE_MIN.getTime()) {
					continue;
				}
				if (i > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.writePropertyStart(outputStream, property.getName(), false);
				writer.writePropertyValue(outputStream, DateTimes.toString(value, DateTimes.FORMAT_DATE));
				writer.writePropertyEnd(outputStream, property.getName(), false);
				count++;
			} else if (property.getValueType() == Boolean.class) {
				Boolean value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				if (i > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.writePropertyStart(outputStream, property.getName(), false);
				writer.writePropertyValue(outputStream, value);
				writer.writePropertyEnd(outputStream, property.getName(), false);
				count++;
			} else if (property.getValueType().isEnum()) {
				Enum<?> value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				if (i > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.writePropertyStart(outputStream, property.getName(), false);
				writer.writePropertyValue(outputStream, value.name());
				writer.writePropertyEnd(outputStream, property.getName(), false);
				count++;
			} else if (property.getValueType() == BigDecimal.class) {
				BigDecimal value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				// 值为零即跳过（compareTo判断，兼容任意scale及BigDecimal.ZERO）
				if (Decimals.isZero(value)) {
					continue;
				}
				if (i > 0) {
					writer.writeDelimiter(outputStream);
				}
				if (value.compareTo(Decimals.VALUE_LONG_MAX_VALUE) > 0
						|| value.compareTo(Decimals.VALUE_LONG_MIN_VALUE) < 0) {
					writer.writePropertyStart(outputStream, property.getName(), false);
					writer.writePropertyValue(outputStream, Strings.valueOf(value));
					writer.writePropertyEnd(outputStream, property.getName(), false);
				} else {
					writer.writePropertyStart(outputStream, property.getName(), false);
					writer.writePropertyValue(outputStream, value);
					writer.writePropertyEnd(outputStream, property.getName(), false);
				}
				count++;
			} else if (property.getValueType() == BigInteger.class) {
				BigInteger value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				// 值为零即跳过（signum判断，兼容任意BigInteger实例）
				if (value.signum() == 0) {
					continue;
				}
				if (i > 0) {
					writer.writeDelimiter(outputStream);
				}
				if (value.compareTo(Decimals.VALUE_LONG_MAX_VALUE.toBigInteger()) > 0
						|| value.compareTo(Decimals.VALUE_LONG_MIN_VALUE.toBigInteger()) < 0) {
					writer.writePropertyStart(outputStream, property.getName(), false);
					writer.writePropertyValue(outputStream, Strings.valueOf(value));
					writer.writePropertyEnd(outputStream, property.getName(), false);
				} else {
					writer.writePropertyStart(outputStream, property.getName(), false);
					writer.writePropertyValue(outputStream, value);
					writer.writePropertyEnd(outputStream, property.getName(), false);
				}
				count++;
			} else if (property.getValueType() == Short.class || property.getValueType() == Integer.class
					|| property.getValueType() == Float.class || property.getValueType() == Double.class
					|| property.getValueType() == Long.class) {
				Object value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				// 值为零即跳过（按数值判断，兼容非缓存的Double/Float实例）
				if (Numbers.isZero(value)) {
					continue;
				}
				if (i > 0) {
					writer.writeDelimiter(outputStream);
				}
				writer.writePropertyStart(outputStream, property.getName(), false);
				writer.writePropertyValue(outputStream, value);
				writer.writePropertyEnd(outputStream, property.getName(), false);
				count++;
			} else if (java.util.List.class.isAssignableFrom(property.getValueType())) {
				List<?> list = object.getProperty(property);
				if (list != null && !list.isEmpty()) {
					if (i > 0) {
						writer.writeDelimiter(outputStream);
					}
					writer.writePropertyStart(outputStream, property.getName(), true);
					for (int j = 0; j < list.size(); j++) {
						if (j > 0) {
							writer.writeDelimiter(outputStream);
						}
						this.serialize((IFieldedObject) list.get(j), outputStream, formated);
					}
					writer.writePropertyEnd(outputStream, property.getName(), true);
					count++;
				}
				list = null;
			} else if (IFieldedObject.class.isAssignableFrom(property.getValueType())) {
				if (i > 0) {
					writer.writeDelimiter(outputStream);
				}
				Object value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				writer.writePropertyStart(outputStream, property.getName(), false);
				this.serialize((IFieldedObject) value, outputStream, formated);
				writer.writePropertyEnd(outputStream, property.getName(), false);
				count++;
			} else {
				throw new SerializationException(I18N.prop("msg_bobas_data_type_not_support", property.getValueType()));
			}
		}
		if (!userProperties.isEmpty()) {
			Object value;
			UserField userField = new UserField();
			if (count > 0) {
				writer.writeDelimiter(outputStream);
			}
			writer.writePropertyStart(outputStream, UserFields.WRAPPER_NAME, true);
			for (int j = 0; j < userProperties.size(); j++) {
				property = userProperties.get(j);
				value = object.getProperty(property);
				if (value == null) {
					continue;
				}
				if (j > 0) {
					writer.writeDelimiter(outputStream);
				}
				userField.setProperty(UserField.PROPERTY_NAME, property.getName());
				userField.setProperty(UserField.PROPERTY_VALUETYPE, property.getValueType().getName());
				userField.setPropertyValue(property, value);
				this.serialize(userField, outputStream, formated);
			}
			writer.writePropertyEnd(outputStream, UserFields.WRAPPER_NAME, true);
		}
		writer.writeObjectEnd(outputStream, object.getClass());
	}

	static class UserField extends FieldedObject {

		private static final long serialVersionUID = 1L;

		public static final IPropertyInfo<String> PROPERTY_NAME = registerProperty("Name", String.class,
				UserField.class);
		public static final IPropertyInfo<String> PROPERTY_VALUETYPE = registerProperty("ValueType", String.class,
				UserField.class);

		private IPropertyInfo<?> propertyValue;

		private Object value;

		public void setPropertyValue(IPropertyInfo<?> property, Object value) {
			this.propertyValue = property;
			this.value = value;
		}

		public Object getPropertyValue() {
			return this.value;
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

}
