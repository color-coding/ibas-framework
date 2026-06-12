package org.colorcoding.ibas.bobas.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 数据库存储过程
 * 
 */
public class SqlStoredProcedure extends SqlPreparedStatement {

	private static final long serialVersionUID = 8400088898134597897L;

	/**
	 * 构造
	 */
	public SqlStoredProcedure() {
	}

	/**
	 * 构造
	 * 
	 * @param name 存储过程名称
	 */
	public SqlStoredProcedure(String name) {
		this();
		this.setName(name);
	}

	private String name;

	/**
	 * 获取存储过程名称
	 *
	 * @return 存储过程名称
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 设置存储过程名称
	 *
	 * @param value 存储过程名称
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * 设置参数值
	 * 
	 * @param name  名称（索引自动编号）
	 * @param value 值
	 */
	public void setObject(String name, Object value) {
		this.setObject(name, value, DataType.UNKNOWN);
	}

	/**
	 * 设置参数值
	 * 
	 * @param name       名称（索引自动编号）
	 * @param value      值
	 * @param targetType 目标类型
	 */
	public void setObject(String name, Object value, DataType targetType) {
		Parameter parameter = this.addParameter(value, targetType);
		parameter.name = name;
	}

	/**
	 * 获取内容（伪代码，拼接名称和参数，非实际SQL语句）
	 *
	 * @return 伪代码字符串
	 */
	@Override
	public String getContent() {
		StringBuilder stringBuilder = new StringBuilder(
				this.getName().length() + this.getParameters().size() * 32 + 32);
		stringBuilder.append(this.getName());
		if (!this.getParameters().isEmpty()) {
			stringBuilder.append(" ");
			// 根据索引排序
			List<Integer> keys = new ArrayList<Integer>(this.getParameters().keySet());
			keys.sort(new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			});
			for (int i = 0; i < keys.size(); i++) {
				if (i > 0) {
					stringBuilder.append(", ");
				}
				stringBuilder.append(this.getParameters().get(keys.get(i)).name);
				stringBuilder.append(" = ");
				stringBuilder.append(this.getParameters().get(keys.get(i)).value);
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 不可用
	 *
	 * @param value 忽略
	 */
	@Override
	public void setContent(String value) {
		throw new UnsupportedOperationException("please input by name or parameters.");
	}

}
