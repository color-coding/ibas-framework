package org.colorcoding.ibas.bobas.serialization.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * JSON格式串输出
 */
public class JsonWriter extends Writer {

	protected static final byte[][] CHAR_BYTES = new byte[128][];
	protected static final int SIGN_INDEX_COLON = 127;

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

	@Override
	protected byte[] bytesOf(char value) {
		if (value >= CHAR_BYTES.length) {
			return null;
		}
		return CHAR_BYTES[value];
	}

	@Override
	public void writeHeader(OutputStream outputStream) throws IOException {
	}

	@Override
	public void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES['{']);
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.write(outputStream, "type");
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		outputStream.write(CHAR_BYTES[':']);
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		String namespace = null, name = null;
		for (Annotation annotation : objectType.getAnnotations()) {
			if (annotation instanceof XmlRootElement) {
				namespace = ((XmlRootElement) annotation).namespace();
				name = ((XmlRootElement) annotation).name();
			} else if (annotation instanceof XmlType) {
				namespace = ((XmlType) annotation).namespace();
				name = ((XmlType) annotation).name();
			}
		}
		if (!Strings.isNullOrEmpty(namespace)) {
		}
		if (!Strings.isNullOrEmpty(name)) {
			this.write(outputStream, name);
		} else {
			this.write(outputStream, objectType.getSimpleName());
		}
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.writeDelimiter(outputStream);
	}

	@Override
	public void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES['}']);
	}

	@Override
	public void writeDelimiter(OutputStream outputStream) throws IOException {
		outputStream.write(CHAR_BYTES[',']);
	}

	@Override
	public void writePropertyValue(OutputStream outputStream, Object value) throws IOException {
		if (value instanceof String) {
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			this.write(outputStream, (String) value);
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		} else {
			this.write(outputStream, Strings.valueOf(value));
		}
	}

	@Override
	public void writePropertyStart(OutputStream outputStream, String property, boolean isArray) throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.write(outputStream, property);
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		outputStream.write(CHAR_BYTES[':']);
		if (isArray) {
			outputStream.write(CHAR_BYTES['[']);
		}
	}

	@Override
	public void writePropertyEnd(OutputStream outputStream, String property, boolean isArray) throws IOException {
		if (isArray) {
			outputStream.write(CHAR_BYTES[']']);
		}
	}

}
