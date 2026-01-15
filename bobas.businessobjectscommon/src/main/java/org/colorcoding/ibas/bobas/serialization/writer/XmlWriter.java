package org.colorcoding.ibas.bobas.serialization.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * XML格式串输出
 */
public class XmlWriter extends Writer {

	protected static final byte[][] CHAR_BYTES = new byte[128][];

	protected static final int SIGN_INDEX_LESS = 0;
	protected static final int SIGN_INDEX_GREATER = 1;
	protected static final int SIGN_INDEX_COLON = 127;

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
	protected byte[] bytesOf(char value) {
		return CHAR_BYTES[value];
	}

	@Override
	public void writeHeader(OutputStream outputStream) throws IOException {
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
		this.write(outputStream, this.getCharset());
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		outputStream.write(CHAR_BYTES[' ']);
		this.write(outputStream, "standalone=");
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.write(outputStream, "yes");
		outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		this.write(outputStream, "?");
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

	@Override
	public void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		String namespace = null, name = null;
		for (Annotation annotation : objectType.getAnnotations()) {
			if (annotation instanceof XmlRootElement) {
				namespace = ((XmlRootElement) annotation).namespace();
				name = ((XmlRootElement) annotation).name();
			} else if (annotation instanceof XmlType) {
				// namespace = ((XmlType) annotation).namespace();
				name = ((XmlType) annotation).name();
			}
		}
		if (!Strings.isNullOrEmpty(namespace)) {
			this.write(outputStream, "ns:");
		}
		if (!Strings.isNullOrEmpty(name)) {
			this.write(outputStream, name);
		} else {
			this.write(outputStream, objectType.getSimpleName());
		}
		if (!Strings.isNullOrEmpty(namespace)) {
			outputStream.write(CHAR_BYTES[' ']);
			this.write(outputStream, "xmlns:ns=");
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			this.write(outputStream, namespace);
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		}
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

	@Override
	public void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		outputStream.write(CHAR_BYTES['/']);

		String namespace = null, name = null;
		for (Annotation annotation : objectType.getAnnotations()) {
			if (annotation instanceof XmlRootElement) {
				namespace = ((XmlRootElement) annotation).namespace();
				name = ((XmlRootElement) annotation).name();
			} else if (annotation instanceof XmlType) {
				// namespace = ((XmlType) annotation).namespace();
				name = ((XmlType) annotation).name();
			}
		}
		if (!Strings.isNullOrEmpty(namespace)) {
			this.write(outputStream, "ns:");
		}
		if (!Strings.isNullOrEmpty(name)) {
			this.write(outputStream, name);
		} else {
			this.write(outputStream, objectType.getSimpleName());
		}
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

	@Override
	public void writeDelimiter(OutputStream outputStream) throws IOException {

	}

	@Override
	public void writePropertyStart(OutputStream outputStream, String property, boolean isArray) throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		this.write(outputStream, property);
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

	@Override
	public void writePropertyValue(OutputStream outputStream, Object value) throws IOException {
		this.write(outputStream, String.valueOf(value));
	}

	@Override
	public void writePropertyEnd(OutputStream outputStream, String property, boolean isArray) throws IOException {
		outputStream.write(CHAR_BYTES[SIGN_INDEX_LESS]);
		outputStream.write(CHAR_BYTES['/']);
		this.write(outputStream, property);
		outputStream.write(CHAR_BYTES[SIGN_INDEX_GREATER]);
	}

}
