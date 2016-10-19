package org.colorcoding.ibas.bobas.messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler implements IHandler {

	/**
	 * 限制大小
	 */
	private long limit = 0;
	/**
	 * 文件名称
	 */
	private String pattern;
	/**
	 * 文件路径
	 */
	private String filePath;

	/**
	 * 追加还是新建默认追加
	 */
	private boolean isAppend = true;

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public String getPattern() {
		if (this.pattern == null || this.pattern.equals("")) {
			this.pattern = "%s";
		}
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isAppend() {
		return isAppend;
	}

	public void setAppend(boolean isAppend) {
		this.isAppend = isAppend;
	}

	public FileHandler(String filePath, boolean isAppend) {
		this(filePath, 0, isAppend);
	}

	public FileHandler(String filePath, long limit, boolean isAppend) {
		this.filePath = filePath;
		this.isAppend = isAppend;
		this.limit = limit;
	}

	@Override
	public void execute(String message) {
		if (message == null || message.equals("")) {
			return;
		}
		File file = this.getCurrentFile(filePath, pattern, 0);
		if (file == null) {
			return;
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, this.isAppend());
			writer.write(message);
			// writer.write(System.getProperty("line.separator"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取当前可用的文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param pattern
	 *            文件名
	 * @param index
	 *            可变序号
	 * @return 可用文件
	 */
	private File getCurrentFile(String filePath, String pattern, int index) {
		if (filePath == null || filePath.equals("")) {
			return null;
		}
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			if (this.getLimit() > 0 && file.length() > this.getLimit()) {
				// 创建新的文件
				index = index + 1;
				String fileName = String.format(this.getPattern(), "." + index);
				filePath = this.getFilePath() + fileName;
				file = this.getCurrentFile(filePath, pattern, index);
			}
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

}
