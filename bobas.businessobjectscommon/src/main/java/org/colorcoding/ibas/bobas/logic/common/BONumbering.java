package org.colorcoding.ibas.bobas.logic.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBOCustomKey;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.IKeyValue;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;
import org.colorcoding.ibas.bobas.db.IDbTableUpdate;

/**
 * 业务对象编号
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BONumbering", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
@XmlRootElement(name = "BONumbering", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public class BONumbering extends BusinessObject<BONumbering> implements IBOCustomKey, IDbTableUpdate {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前类型
	 */
	private static final Class<?> MY_CLASS = BONumbering.class;

	/**
	 * 数据库表
	 */
	public static final String DB_TABLE_NAME = "${Company}_SYS_ONNM";

	/**
	 * 属性名称-对象编码
	 */
	private static final String PROPERTY_OBJECTCODE_NAME = "ObjectCode";

	/**
	 * 对象编码 属性
	 */
	@DbField(name = "ObjectCode", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME, primaryKey = true)
	public static final IPropertyInfo<String> PROPERTY_OBJECTCODE = registerProperty(PROPERTY_OBJECTCODE_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-对象编码
	 * 
	 * @return 值
	 */
	public final String getObjectCode() {
		return this.getProperty(PROPERTY_OBJECTCODE);
	}

	/**
	 * 设置-对象编码
	 * 
	 * @param value 值
	 */
	public final void setObjectCode(String value) {
		this.setProperty(PROPERTY_OBJECTCODE, value);
	}

	/**
	 * 属性名称-自动序号
	 */
	private static final String PROPERTY_AUTOKEY_NAME = "AutoKey";

	/**
	 * 自动序号 属性
	 */
	@DbField(name = "AutoKey", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<Integer> PROPERTY_AUTOKEY = registerProperty(PROPERTY_AUTOKEY_NAME, Integer.class,
			MY_CLASS);

	/**
	 * 获取-自动序号
	 * 
	 * @return 值
	 */
	public final Integer getAutoKey() {
		return this.getProperty(PROPERTY_AUTOKEY);
	}

	/**
	 * 设置-自动序号
	 * 
	 * @param value 值
	 */
	public final void setAutoKey(Integer value) {
		this.setProperty(PROPERTY_AUTOKEY, value);
	}

	private List<BOSeriesNumbering> seriesNumbering;

	/**
	 * 获取-业务对象序列编号方式
	 * 
	 * @return 值
	 */
	public final List<BOSeriesNumbering> getSeriesNumberings() {
		if (this.seriesNumbering == null) {
			this.seriesNumbering = new ArrayList<>();
		}
		return this.seriesNumbering;
	}

	private List<IKeyValue> maxValueNumberings;

	/**
	 * 获取-业务最大编号
	 * 
	 * @return 值
	 */
	public final List<IKeyValue> getMaxValueNumbering() {
		if (this.maxValueNumberings == null) {
			this.maxValueNumberings = new ArrayList<>();
		}
		return this.maxValueNumberings;
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();
	}
}
