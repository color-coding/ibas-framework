package org.colorcoding.ibas.bobas.commands;

/**
 * 参数
 * 
 * @author Niuren.Zhu
 *
 */
public class Argument {

	public Argument() {

	}

	public Argument(String name, String description) {
		this.setName(name);
		this.setDescription(description);
	}

	private String name;

	/**
	 * 参数名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null && !name.startsWith("-")) {
			name = "-" + name;
		}
		this.name = name;
	}

	private String type;

	/**
	 * 参数的类型（:type）
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String value;

	/**
	 * 参数的值
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String original;

	/**
	 * 参数的原始命令符
	 * 
	 * @return
	 */
	public String getOriginal() {
		return original;
	}

	private boolean isInputed;

	/**
	 * 是否输入了此参数
	 * 
	 * @return
	 */
	public boolean isInputed() {
		return isInputed;
	}

	public void setInputed(boolean isInputed) {
		this.isInputed = isInputed;
	}

	public void setOriginal(String original) {
		this.original = original;
		this.setInputed(true);
		if (this.original != null && !this.original.isEmpty()) {
			String[] tmps = this.original.split("=");
			if (tmps.length >= 1) {
				String tmp = tmps[0];
				if (tmp.indexOf(":") > 0) {
					this.setType(tmp.split(":")[1]);
				}
			}
			if (tmps.length >= 2) {
				String tmp = tmps[1];
				this.setValue(tmp);
			}
		}
	}

	private String description;

	/**
	 * 参数的描述
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return String.format("Argument %s %s", this.getName(), this.getValue());
	}

	/**
	 * 判断字符串是否为当前变量
	 * 
	 * @param string
	 *            分析的字符串
	 * @return
	 */
	public boolean check(String string) {
		if (string == null || string.isEmpty()) {
			return false;
		}
		String tmpName = string.split("=")[0].split(":")[0];
		if (this.getName().equalsIgnoreCase(tmpName)) {
			return true;
		}
		return false;
	}
}
