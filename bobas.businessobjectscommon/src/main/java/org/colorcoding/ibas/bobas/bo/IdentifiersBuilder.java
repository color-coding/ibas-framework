package org.colorcoding.ibas.bobas.bo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IFieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 业务对象标识构建器。
 *
 * <p>
 * 使用对象编码和有序的唯一属性构建标识。标识格式与
 * {@link BusinessObject#getIdentifiers()} 保持一致：
 * {@code {[ObjectCode].[Property = Value]&[Property = Value]}}。
 * </p>
 */
public final class IdentifiersBuilder {

	private String objectCode;
	private final Map<String, Object> properties = new LinkedHashMap<>();

	private IdentifiersBuilder() {
	}

	/**
	 * 创建标识构建器。
	 *
	 * @return 标识构建器
	 */
	public static IdentifiersBuilder create() {
		return new IdentifiersBuilder();
	}

	/**
	 * 创建单据或单据行的标识构建器。
	 *
	 * @param objectCode 对象编码
	 * @param docEntry   单据主键
	 * @param lineId     行主键；小于等于零或null时忽略
	 * @return 已填充单据主键的标识构建器
	 */
	public static IdentifiersBuilder document(String objectCode, Integer docEntry, Integer lineId) {
		IdentifiersBuilder builder = create().objectCode(objectCode)
				.property(IBODocument.MASTER_PRIMARY_KEY_NAME, docEntry);
		if (lineId != null && lineId > 0) {
			builder.property(IBOLine.SECONDARY_PRIMARY_KEY_NAME, lineId);
		}
		return builder;
	}

	/**
	 * 根据业务对象创建标识构建器。
	 *
	 * <p>
	 * 主数据使用 {@link IBOMasterData#MASTER_PRIMARY_KEY_NAME}；其他字段对象使用
	 * {@link IPropertyInfo#isPrimaryKey()} 标记的属性，并保留对象属性定义的顺序。
	 * </p>
	 *
	 * @param bo 业务对象
	 * @return 已填充对象编码及唯一属性的标识构建器
	 */
	public static IdentifiersBuilder from(IBusinessObject bo) {
		if (bo == null) {
			throw new IllegalArgumentException("Argument [bo] can not be null.");
		}
		IdentifiersBuilder builder = create();
		if (bo instanceof IBOStorageTag) {
			builder.objectCode(((IBOStorageTag) bo).getObjectCode());
		} else {
			builder.objectCode(bo.getClass().getSimpleName());
		}
		if (bo instanceof IBOMasterData) {
			builder.property(IBOMasterData.MASTER_PRIMARY_KEY_NAME, ((IBOMasterData) bo).getCode());
		} else if (bo instanceof IFieldedObject) {
			IFieldedObject fieldedObject = (IFieldedObject) bo;
			for (IPropertyInfo<?> property : fieldedObject.properties()) {
				if (property.isPrimaryKey()) {
					builder.property(property, fieldedObject.getProperty(property));
				}
			}
		}
		return builder;
	}

	/**
	 * 设置对象编码。
	 *
	 * @param value 对象编码
	 * @return 当前构建器
	 */
	public IdentifiersBuilder objectCode(String value) {
		this.objectCode = value;
		return this;
	}

	/**
	 * 添加唯一属性。重复属性名使用最后一次传入的值，属性位置不变。
	 *
	 * @param property 属性信息
	 * @param value    属性值
	 * @return 当前构建器
	 */
	public IdentifiersBuilder property(IPropertyInfo<?> property, Object value) {
		if (property == null) {
			throw new IllegalArgumentException("Argument [property] can not be null.");
		}
		return this.property(property.getName(), value);
	}

	/**
	 * 添加唯一属性。
	 *
	 * @param name  属性名
	 * @param value 属性值
	 * @return 当前构建器
	 */
	public IdentifiersBuilder property(String name, Object value) {
		if (Strings.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("Argument [name] can not be null or empty.");
		}
		this.properties.put(name, value);
		return this;
	}

	/**
	 * 构建业务对象标识。
	 *
	 * @return 业务对象标识
	 */
	public String build() {
		if (Strings.isNullOrEmpty(this.objectCode)) {
			throw new IllegalStateException("Object code is required.");
		}
		StringBuilder builder = new StringBuilder(96);
		builder.append("{[").append(this.objectCode).append("].");
		boolean first = true;
		for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
			if (!first) {
				builder.append("&");
			}
			builder.append("[").append(entry.getKey()).append(" = ")
					.append(Strings.valueOf(entry.getValue())).append("]");
			first = false;
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public String toString() {
		return this.build();
	}
}
