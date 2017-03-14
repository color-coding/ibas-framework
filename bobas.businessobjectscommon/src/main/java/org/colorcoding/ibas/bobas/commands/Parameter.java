package org.colorcoding.ibas.bobas.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 参数
 * 
 * @author Niuren.Zhu
 *
 */
public class Parameter {
	public static Parameter create(String string) {
		// {"name":"ibasVersion","value":"0.1.1"}
		if (string == null || string.isEmpty()) {
			return null;
		}
		String name = null;
		String value = null;
		String[] tmps = string.replace("{", "").replace("}", "").split(",");
		for (String tmp : tmps) {
			String[] items = tmp.replace("\"", "").split(":");
			if (items.length == 2) {
				if (items[0].equalsIgnoreCase("name")) {
					name = items[1];
				}
				if (items[0].equalsIgnoreCase("value")) {
					value = items[1];
				}
			}
		}
		if (name != null && value != null) {
			return new Parameter(name, value);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Parameter) {
			return this.equals((Parameter) obj);
		}
		return super.equals(obj);
	}

	public boolean equals(Parameter obj) {
		if (obj != null) {
			return this.getName().equals(obj.getName());
		}
		return false;
	}

	public Parameter() {

	}

	public Parameter(String name, Object value) {
		this.setName(name);
		this.setValue(value);
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	protected Object getValue(Object value, String path)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (path != null && !path.isEmpty() && value != null) {
			String curPath = path;
			String nextPath = null;
			int indexPath = path.indexOf(").");// 仅支持方法，所以为此
			if (indexPath > 0) {
				curPath = path.substring(0, indexPath + 1);
				nextPath = path.substring(indexPath + 2, path.length());
			}
			// 获取当前路径参数
			Object tmpValue = null;
			Class<?> type = value.getClass();
			String mName = curPath;
			Object[] pathArgs = {};
			if (curPath.indexOf("(") > 0) {
				// 存在参数
				mName = curPath.substring(0, curPath.indexOf("("));
				String tmp = curPath.substring(curPath.indexOf("(") + 1, curPath.indexOf(")"));
				if (tmp.length() > 0) {
					if (tmp.length() == 1) {
						pathArgs = new Object[1];
						pathArgs[0] = tmp;
					} else {
						String[] tmps = tmp.split(",");
						pathArgs = new Object[tmps.length];
						for (int i = 0; i < tmps.length; i++) {
							pathArgs[i] = tmps[i];
						}
					}
				}
			}
			for (Method method : type.getMethods()) {
				if (method.getParameterCount() == pathArgs.length
						&& (method.getName().equals(mName) || method.getName().equalsIgnoreCase("get" + mName))) {
					tmpValue = method.invoke(value, pathArgs);
					break;
				}
			}
			// 处理下级路径
			if (nextPath != null && !nextPath.isEmpty()) {
				return this.getValue(tmpValue, nextPath);
			}
			// 路径值未找到
			return tmpValue;
		}
		return value;
	}

	public Object getValue(String path) throws Exception {
		// like,getValue().toString(xml)
		return this.getValue(this.getValue(), path);
	}

	public String toString() {
		return String.format("Parameter %s %s", this.getName(), this.getValue());
	}
}
