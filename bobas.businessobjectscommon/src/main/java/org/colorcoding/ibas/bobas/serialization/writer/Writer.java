package org.colorcoding.ibas.bobas.serialization.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 数据串书写员
 */
public abstract class Writer {

	private String charset;

	public String getCharset() {
		if (Strings.isNullOrEmpty(this.charset)) {
			this.charset = "utf-8";
		}
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void write(OutputStream outputStream, String value) throws IOException {
		if (value == null || value.isEmpty()) {
			return;
		}
		char cValue;
		byte[] bytes;
		int index = -1;
		for (int i = 0; i < value.length(); i++) {
			cValue = value.charAt(i);
			// 控制字符跳过
			if (cValue < 32 || cValue == 127) {
				continue;
			}
			if (cValue < 128) {
				if (index > -1) {
					outputStream.write(value.substring(index, i).getBytes(this.getCharset()));
					index = -1;
				}
				bytes = this.bytesOf(cValue);
				if (bytes == null) {
					continue;
				}
				if (bytes.length == 0) {
					continue;
				}
				outputStream.write(bytes);
			} else if (index == -1) {
				index = i;
			}
		}
		if (index > -1) {
			outputStream.write(value.substring(index).getBytes(this.getCharset()));
		}
	}

	public void writeNewLine(OutputStream outputStream) throws IOException {
		outputStream.write('\n');
	}

	protected abstract byte[] bytesOf(char value);

	public abstract void writeHeader(OutputStream outputStream) throws IOException;

	public abstract void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException;

	public abstract void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException;

	public abstract void writeDelimiter(OutputStream outputStream) throws IOException;

	public abstract void writePropertyStart(OutputStream outputStream, String property, boolean isArray)
			throws IOException;

	public abstract void writePropertyValue(OutputStream outputStream, Object value) throws IOException;

	public abstract void writePropertyEnd(OutputStream outputStream, String property, boolean isArray)
			throws IOException;
}
