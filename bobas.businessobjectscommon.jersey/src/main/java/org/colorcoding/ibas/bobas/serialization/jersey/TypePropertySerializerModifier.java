package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.IOException;

import org.colorcoding.ibas.bobas.core.Serializable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

//自定义通用序列化器
class GenericSerializer extends JsonSerializer<Object> {
	private final BeanSerializerBase defaultSerializer;

	public GenericSerializer(BeanSerializerBase defaultSerializer) {
		this.defaultSerializer = defaultSerializer;
	}

	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (value instanceof Serializable) {
			jsonGenerator.writeStartObject();
			// 添加 type 属性
			jsonGenerator.writeStringField("type", value.getClass().getSimpleName());

			// 调用默认序列化器序列化对象的其他属性
			this.defaultSerializer.unwrappingSerializer(null).serialize(value, jsonGenerator, serializerProvider);

			jsonGenerator.writeEndObject();
		} else {
			this.defaultSerializer.serialize(value, jsonGenerator, serializerProvider);
		}
	}
}

//自定义 BeanSerializerModifier
class AllTypesSerializerModifier extends BeanSerializerModifier {

	private static final long serialVersionUID = 1L;

	@Override
	public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
			JsonSerializer<?> serializer) {
		if (serializer instanceof BeanSerializerBase) {
			return new GenericSerializer((BeanSerializerBase) serializer);
		}
		return serializer;
	}
}
