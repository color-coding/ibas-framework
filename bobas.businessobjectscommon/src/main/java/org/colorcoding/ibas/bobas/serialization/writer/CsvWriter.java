package org.colorcoding.ibas.bobas.serialization.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * CSV格式串输出
 */
public class CsvWriter extends Writer {

	protected static final byte[][] CHAR_BYTES = new byte[128][];
	protected static final int SIGN_INDEX_COLON = 127;

	static {
		CHAR_BYTES['"'] = "\"\"".getBytes();
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
	public void writeDelimiter(OutputStream outputStream) throws IOException {
		outputStream.write(CHAR_BYTES[',']);
	}

	public void write(OutputStream outputStream, String value) throws IOException {
		if (value == null || value.isEmpty()) {
			return;
		}
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			super.write(outputStream, value);
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		} else {
			super.write(outputStream, value);
		}
	}

	@Override
	public void writeHeader(OutputStream outputStream) throws IOException {
	}

	@Override
	public void writeObjectStart(OutputStream outputStream, Class<?> objectType) throws IOException {
	}

	@Override
	public void writeObjectEnd(OutputStream outputStream, Class<?> objectType) throws IOException {
	}

	@Override
	public void writePropertyStart(OutputStream outputStream, String property, boolean isArray) throws IOException {
	}

	@Override
	public void writePropertyValue(OutputStream outputStream, Object value) throws IOException {
		this.write(outputStream, Strings.valueOf(value));
	}

	@Override
	public void writePropertyEnd(OutputStream outputStream, String property, boolean isArray) throws IOException {
	}

}
