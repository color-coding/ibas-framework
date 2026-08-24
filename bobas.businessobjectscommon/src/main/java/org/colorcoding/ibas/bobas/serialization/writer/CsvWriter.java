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
	protected static final byte[] UTF8_BOM = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

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
		if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
			// CSV字段中的换行等控制字符必须保留；基类Writer会跳过控制字符，不能直接调用。
			outputStream.write(value.replace("\"", "\"\"").getBytes(this.getCharset()));
			outputStream.write(CHAR_BYTES[SIGN_INDEX_COLON]);
		} else {
			outputStream.write(value.getBytes(this.getCharset()));
		}
	}

	@Override
	public void writeHeader(OutputStream outputStream) throws IOException {
		// UTF-8 CSV写入BOM，便于Excel等工具正确识别编码
		if ("UTF-8".equalsIgnoreCase(this.getCharset())) {
			outputStream.write(UTF8_BOM);
		}
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
