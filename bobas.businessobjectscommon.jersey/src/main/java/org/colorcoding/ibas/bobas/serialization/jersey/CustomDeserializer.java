package org.colorcoding.ibas.bobas.serialization.jersey;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * 自定义通用序列化器
 */
class GenericDeserializer extends JsonDeserializer<Object> {
	private final BeanDeserializerBase defaultDeserializer;

	public GenericDeserializer(BeanDeserializerBase defaultDeserializer) {
		this.defaultDeserializer = defaultDeserializer;
	}

	@Override
	public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
			throws IOException, JacksonException {
		// TODO Auto-generated method stub
		return super.deserializeWithType(p, ctxt, typeDeserializer);
	}

	@Override
	public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer,
			Object intoValue) throws IOException, JacksonException {
		// TODO Auto-generated method stub
		return super.deserializeWithType(p, ctxt, typeDeserializer, intoValue);
	}

	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		return this.defaultDeserializer.deserialize(p, ctxt);
	}

}

/**
 * 自定义 BeanDeserializerModifier
 */
class AllTypesDeserializerModifier extends BeanDeserializerModifier {

	private static final long serialVersionUID = 1L;

	@Override
	public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
			JsonDeserializer<?> deserializer) {
		if (deserializer instanceof BeanDeserializerBase) {
			return new GenericDeserializer((BeanDeserializerBase) deserializer);
		}
		return super.modifyDeserializer(config, beanDesc, deserializer);
	}

	@Override
	public JsonDeserializer<?> modifyArrayDeserializer(DeserializationConfig config, ArrayType valueType,
			BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
		// TODO Auto-generated method stub
		return super.modifyArrayDeserializer(config, valueType, beanDesc, deserializer);
	}

	@Override
	public JsonDeserializer<?> modifyCollectionDeserializer(DeserializationConfig config, CollectionType type,
			BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
		// TODO Auto-generated method stub
		return super.modifyCollectionDeserializer(config, type, beanDesc, deserializer);
	}

	@Override
	public JsonDeserializer<?> modifyCollectionLikeDeserializer(DeserializationConfig config, CollectionLikeType type,
			BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
		// TODO Auto-generated method stub
		return super.modifyCollectionLikeDeserializer(config, type, beanDesc, deserializer);
	}

}
