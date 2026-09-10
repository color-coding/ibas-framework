package org.colorcoding.ibas.bobas.bo;

import java.util.LinkedHashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.core.IFieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 业务对象引用构建器。
 *
 * <p>
 * 支持生成带属性值的业务对象标识，以及用于消息描述的无值属性引用：
 * {@code {[ObjectCode].[Property = Value]}} 和 {@code {[ObjectCode].[Property]}}。
 * </p>
 */
public final class BOIdentifierBuilder {

	private String objectCode;
	private final Map<String, Object> properties = new LinkedHashMap<>();

	private BOIdentifierBuilder() {
	}

	/**
	 * 创建引用构建器。
	 *
	 * @return 引用构建器
	 */
	public static BOIdentifierBuilder create() {
		return new BOIdentifierBuilder();
	}

	/**
	 * 创建仅包含业务对象编码的引用构建器。
	 *
	 * @param bo 业务对象
	 * @return 已设置对象编码的引用构建器
	 */
	public static BOIdentifierBuilder create(IBusinessObject bo) {
		if (bo == null) {
			throw new IllegalArgumentException("Argument [bo] can not be null.");
		}
		return create().objectCode(objectCodeOf(bo));
	}

	/**
	 * 创建单据或单据行的引用构建器。
	 *
	 * @param objectCode 对象编码
	 * @param docEntry   单据主键
	 * @param lineId     行主键；小于等于零或null时忽略
	 * @return 已填充单据主键的引用构建器
	 */
	public static BOIdentifierBuilder document(String objectCode, Integer docEntry, Integer lineId) {
		BOIdentifierBuilder builder = create().objectCode(objectCode)
				.property(IBODocument.MASTER_PRIMARY_KEY_NAME, docEntry);
		if (lineId != null && lineId > 0) {
			builder.property(IBOLine.SECONDARY_PRIMARY_KEY_NAME, lineId);
		}
		return builder;
	}

	/**
	 * 创建主数据标识构建器。
	 *
	 * @param objectCode 对象编码
	 * @param code       主数据编码
	 * @return 已填充主数据主键的标识构建器
	 */
	public static BOIdentifierBuilder masterData(String objectCode, String code) {
		return create().objectCode(objectCode)
				.property(IBOMasterData.MASTER_PRIMARY_KEY_NAME, code);
	}

	/**
	 * 创建简单对象标识构建器。
	 *
	 * @param objectCode 对象编码
	 * @param objectKey  简单对象主键
	 * @return 已填充简单对象主键的标识构建器
	 */
	public static BOIdentifierBuilder simple(String objectCode, Integer objectKey) {
		return create().objectCode(objectCode)
				.property(IBOSimple.MASTER_PRIMARY_KEY_NAME, objectKey);
	}

	/**
	 * 根据业务对象创建包含主键属性的引用构建器。
	 *
	 * @param bo 业务对象
	 * @return 已填充对象编码及唯一属性的引用构建器
	 */
	public static BOIdentifierBuilder from(IBusinessObject bo) {
		if (bo == null) {
			throw new IllegalArgumentException("Argument [bo] can not be null.");
		}
		BOIdentifierBuilder builder = create(bo);
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

	private static String objectCodeOf(IBusinessObject bo) {
		if (bo instanceof IBOStorageTag) {
			String objectCode = ((IBOStorageTag) bo).getObjectCode();
			if (!Strings.isNullOrEmpty(objectCode)) {
				return objectCode;
			}
		}
		return bo.getClass().getSimpleName();
	}

	/** 设置对象编码。 */
	public BOIdentifierBuilder objectCode(String value) {
		this.objectCode = value;
		return this;
	}

	/** 添加带值属性。 */
	public BOIdentifierBuilder property(IPropertyInfo<?> property, Object value) {
		if (property == null) {
			throw new IllegalArgumentException("Argument [property] can not be null.");
		}
		return this.property(property.getName(), value);
	}

	/** 添加无值属性，用于前端通过语言资源解析属性描述。 */
	public BOIdentifierBuilder property(IPropertyInfo<?> property) {
		if (property == null) {
			throw new IllegalArgumentException("Argument [property] can not be null.");
		}
		return this.property(property.getName());
	}

	/** 添加无值属性，用于前端通过语言资源解析属性描述。 */
	public BOIdentifierBuilder property(String name) {
		if (Strings.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("Argument [name] can not be null or empty.");
		}
		this.properties.put(name, null);
		return this;
	}

	/** 添加带值属性。 */
	public BOIdentifierBuilder property(String name, Object value) {
		if (Strings.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("Argument [name] can not be null or empty.");
		}
		this.properties.put(name, value == null ? Strings.VALUE_EMPTY : value);
		return this;
	}

	/**
	 * 构建业务对象引用。
	 *
	 * @return 业务对象引用
	 */
	public String build() {
		if (Strings.isNullOrEmpty(this.objectCode)) {
			throw new IllegalStateException("Object code is required.");
		}
		String objectCode = Configuration.applyVariables(this.objectCode);
		StringBuilder builder = new StringBuilder(96);
		builder.append("{[").append(objectCode).append("]");
		if (this.properties.isEmpty()) {
			return builder.append("}").toString();
		}
		builder.append(".");
		boolean first = true;
		for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
			if (!first) {
				builder.append("&");
			}
			builder.append("[").append(entry.getKey());
			if (entry.getValue() != null) {
				builder.append(" = ").append(Strings.valueOf(entry.getValue()));
			}
			builder.append("]");
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
