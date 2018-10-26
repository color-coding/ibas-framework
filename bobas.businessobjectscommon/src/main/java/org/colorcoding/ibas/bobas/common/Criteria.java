package org.colorcoding.ibas.bobas.common;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.UserFieldManager;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfoManager;
import org.colorcoding.ibas.bobas.db.DataConvert;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.ISerializerManager;
import org.colorcoding.ibas.bobas.serialization.Serializable;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;

/**
 * 查询
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Criteria", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Criteria", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class Criteria extends Serializable implements ICriteria, Cloneable {

	private static final long serialVersionUID = -2340536835606017565L;

	/**
	 * 创建实例
	 * 
	 * @return
	 */
	public static ICriteria create() {
		return new Criteria();
	}

	/**
	 * 创建实例
	 * 
	 * @param value 值
	 * @return
	 */
	public static ICriteria create(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (value.startsWith("{[") && value.endsWith("]}")) {
			// 对象识别码
			return fromIdentifiers(value);
		} else {
			ISerializerManager manager = SerializerFactory.create().createManager();
			if (value.startsWith("<?xml")) {
				// xml
				ISerializer<?> serializer = manager.create(ISerializerManager.TYPE_XML);
				return (ICriteria) serializer.deserialize(value, Criteria.class, Conditions.class, Condition.class,
						Sorts.class, Sort.class, ChildCriterias.class, ChildCriteria.class);
			} else if (value.startsWith("{") && value.endsWith("}")) {
				// json
				ISerializer<?> serializer = manager.create(ISerializerManager.TYPE_JSON);
				return (ICriteria) serializer.deserialize(value, Criteria.class, Conditions.class, Condition.class,
						Sorts.class, Sort.class, ChildCriterias.class, ChildCriteria.class);
			}
		}
		return null;
	}

	/**
	 * 根据识别码创建查询
	 * 
	 * {[CC_TT_SALESORDER].[DocEntry = 1]&[LineId = 2]}
	 * 
	 * @param identifiers
	 * @return
	 */
	private static ICriteria fromIdentifiers(String identifiers) {
		Criteria criteria = new Criteria();
		String[] tmps = identifiers.split("\\]\\.\\[");
		criteria.setBusinessObject(tmps[0].replace("{[", ""));
		tmps = tmps[1].split("\\]\\&\\[");
		for (int i = 0; i < tmps.length; i++) {
			String[] tmpFields = tmps[i].split("=");
			if (tmpFields.length != 2) {
				continue;
			}
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(tmpFields[0].replace("[", "").trim());
			condition.setValue(tmpFields[1].replace("]", "").replace("}", "").trim());
		}
		return criteria;
	}

	private String businessObject = "";

	@Override
	@XmlElement(name = "BusinessObject")
	public final String getBusinessObject() {
		if (this.businessObject == null) {
			this.businessObject = "";
		}
		return this.businessObject;
	}

	@Override
	public final void setBusinessObject(String value) {
		this.businessObject = value;
	}

	private int resultCount = -1;

	@Override
	@XmlElement(name = "ResultCount")
	public final int getResultCount() {
		return this.resultCount;
	}

	@Override
	public final void setResultCount(int value) {
		this.resultCount = value;
	}

	private boolean noChilds = false;

	@Override
	@XmlElement(name = "NoChilds")
	public final boolean isNoChilds() {
		return this.noChilds;
	}

	@Override
	public final void setNoChilds(boolean value) {
		this.noChilds = value;
	}

	private String remarks = "";

	@Override
	@XmlElement(name = "Remarks")
	public final String getRemarks() {
		return this.remarks;
	}

	@Override
	public final void setRemarks(String value) {
		this.remarks = value;
	}

	private IConditions conditions = null;

	@Override
	@XmlElementWrapper(name = "Conditions")
	@XmlElement(name = "Condition", type = Condition.class)
	public final IConditions getConditions() {
		if (this.conditions == null) {
			this.conditions = new Conditions();
		}
		return this.conditions;
	}

	public final void setConditions(IConditions value) {
		this.conditions = value;
	}

	private IChildCriterias childCriterias = null;

	@Override
	@XmlElementWrapper(name = "ChildCriterias")
	@XmlElement(name = "ChildCriteria", type = ChildCriteria.class, nillable = true)
	public final IChildCriterias getChildCriterias() {
		if (this.childCriterias == null) {
			this.childCriterias = new ChildCriterias();
		}
		return this.childCriterias;
	}

	public final void setChildCriterias(IChildCriterias value) {
		this.childCriterias = value;
	}

	private ISorts sorts = null;

	@Override
	@XmlElementWrapper(name = "Sorts")
	@XmlElement(name = "Sort", type = Sort.class, nillable = true)
	public final ISorts getSorts() {
		if (this.sorts == null) {
			this.sorts = new Sorts();
		}
		return this.sorts;
	}

	public final void setSorts(ISorts value) {
		this.sorts = value;
	}

	@Override
	public final ICriteria clone() {
		ISerializer<?> serializer = SerializerFactory.create().createManager().create();
		return (ICriteria) serializer.clone(this);
	}

	@Override
	public String toString(String type) {
		ISerializer<?> serializer = SerializerFactory.create().createManager().create(type);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(this, writer);
		return writer.toString();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{criteria: ");
		if (this.getBusinessObject() != null && !this.getBusinessObject().isEmpty()) {
			stringBuilder.append(this.getBusinessObject());
			stringBuilder.append(", ");
		}
		stringBuilder.append("result|");
		stringBuilder.append(this.getResultCount());
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	protected ICriteria boCriteria(IBusinessObjectBase bo, ConditionOperation operation) {
		ICriteria boCriteria = null;
		// 判断BO类型，添加下个集合条件，尽量使用数值字段
		if (bo instanceof IBOSimple) {
			IBOSimple simple = (IBOSimple) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(simple.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBOSimple.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(simple.getObjectKey());
			condition.setOperation(operation);
		} else if (bo instanceof IBODocument) {
			IBODocument document = (IBODocument) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(document.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBODocument.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(document.getDocEntry());
			condition.setOperation(operation);
		} else if (bo instanceof IBOMasterData) {
			IBOMasterData masterData = (IBOMasterData) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(masterData.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBOMasterData.SERIAL_NUMBER_KEY_NAME);
			condition.setValue(masterData.getDocEntry());
			condition.setOperation(operation);
		} else if (bo instanceof IBOSimpleLine) {
			IBOSimpleLine line = (IBOSimpleLine) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(line.getObjectCode());
			// 父项相等时，lineid比较方式
			ICondition condition = boCriteria.getConditions().create();
			condition.setBracketOpen(2);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getObjectKey());
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
			condition.setValue(line.getLineId());
			condition.setOperation(operation);
			// 父项不等时
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getObjectKey());
			condition.setOperation(operation);
		} else if (bo instanceof IBODocumentLine) {
			IBODocumentLine line = (IBODocumentLine) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(line.getObjectCode());
			// 父项相等时，lineid比较方式
			ICondition condition = boCriteria.getConditions().create();
			condition.setBracketOpen(2);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getDocEntry());
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
			condition.setValue(line.getLineId());
			condition.setOperation(operation);
			// 父项不等时
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getDocEntry());
			condition.setOperation(operation);
		} else if (bo instanceof IBOMasterDataLine) {
			IBOMasterDataLine line = (IBOMasterDataLine) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(line.getObjectCode());
			// 父项相等时，lineid比较方式
			ICondition condition = boCriteria.getConditions().create();
			condition.setBracketOpen(2);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getCode());
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
			condition.setValue(line.getLineId());
			condition.setOperation(operation);
			// 父项不等时
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getCode());
			condition.setOperation(operation);
		}
		if (boCriteria == null) {
			boCriteria = bo.getCriteria();
			for (ICondition condition : boCriteria.getConditions()) {
				condition.setOperation(operation);
			}
		}
		return boCriteria;
	}

	@Override
	public final ICriteria next(IBusinessObjectBase lastBO) {
		if (lastBO != null) {
			SortType sortType = SortType.ASCENDING;
			ConditionOperation operation = ConditionOperation.GRATER_THAN;
			if (this.getSorts().size() > 0) {
				sortType = this.getSorts().firstOrDefault().getSortType();
			}
			if (sortType == SortType.DESCENDING) {
				operation = ConditionOperation.LESS_THAN;
			}
			ICriteria boCriteria = this.boCriteria(lastBO, operation);
			if (boCriteria == null) {
				return null;
			}
			return this.copyFrom(boCriteria);
		}
		return null;
	}

	@Override
	public final ICriteria previous(IBusinessObjectBase firstBO) {
		if (firstBO != null) {
			SortType sortType = SortType.ASCENDING;
			ConditionOperation operation = ConditionOperation.LESS_THAN;
			if (this.getSorts().size() > 0) {
				sortType = this.getSorts().firstOrDefault().getSortType();
			}
			if (sortType == SortType.DESCENDING) {
				operation = ConditionOperation.GRATER_THAN;
			}
			ICriteria boCriteria = this.boCriteria(firstBO, operation);
			if (boCriteria == null) {
				return null;
			}
			return this.copyFrom(boCriteria);
		}
		return null;
	}

	@Override
	public final ICriteria copyFrom(ICriteria criteria) {
		ICriteria nCriteria = this.clone();
		if (criteria != null) {
			ICriteria tmpCriteria = criteria.clone();
			// 复制子项查询
			for (IChildCriteria tmpChildCriteria : tmpCriteria.getChildCriterias()) {
				if (tmpChildCriteria.getPropertyPath() == null) {
					continue;
				}
				boolean isNew = true;
				for (IChildCriteria myChildCriteria : nCriteria.getChildCriterias()) {
					if (myChildCriteria.getPropertyPath() == null) {
						continue;
					}
					if (myChildCriteria.getPropertyPath().equals(tmpChildCriteria.getPropertyPath())) {
						isNew = false;
						break;
					}
				}
				if (isNew) {
					nCriteria.getChildCriterias().add(tmpChildCriteria);
				}
			}
			// 复制查询条件
			if (nCriteria.getConditions().size() > 0) {
				// 原始条件括号括起
				ICondition condition = nCriteria.getConditions().get(0);
				condition.setBracketOpen(condition.getBracketOpen() + 1);
				condition = nCriteria.getConditions().get(nCriteria.getConditions().size() - 1);
				condition.setBracketClose(condition.getBracketClose() + 1);
			}
			if (tmpCriteria.getConditions().size() > 0) {
				// 拷贝条件括号括起
				ICondition condition = tmpCriteria.getConditions().get(0);
				condition.setBracketOpen(condition.getBracketOpen() + 1);
				condition = tmpCriteria.getConditions().get(tmpCriteria.getConditions().size() - 1);
				condition.setBracketClose(condition.getBracketClose() + 1);
			}
			nCriteria.getConditions().addAll(tmpCriteria.getConditions());
			// 复制排序条件
			for (ISort tmpSort : tmpCriteria.getSorts()) {
				if (tmpSort.getAlias() == null) {
					continue;
				}
				boolean isNew = true;
				for (ISort mySort : nCriteria.getSorts()) {
					if (mySort.getAlias() == null) {
						continue;
					}
					if (mySort.getAlias().equals(tmpSort.getAlias())) {
						isNew = false;
						break;
					}
				}
				if (isNew) {
					nCriteria.getSorts().add(tmpSort);
				}
			}
		}
		return nCriteria;
	}

	@Override
	public void check(Class<?> type) {
		if (type == null) {
			return;
		}
		// 检查属性
		this.check(PropertyInfoManager.getPropertyInfoList(type));
		// 检查用户字段
		this.check(UserFieldManager.getUserFieldInfoList(type));
	}

	/**
	 * 检查排序查询条件
	 * 
	 * @param propertyInfos
	 */
	protected void check(Collection<IPropertyInfo<?>> propertyInfos) {
		if (propertyInfos == null || propertyInfos.isEmpty()) {
			return;
		}
		for (IPropertyInfo<?> item : propertyInfos) {
			if (item.getName() == null || item.getName().isEmpty()) {
				continue;
			}
			if (!(item instanceof PropertyInfo<?>)) {
				continue;
			}
			PropertyInfo<?> propertyInfo = (PropertyInfo<?>) item;
			Object annotation = propertyInfo.getAnnotation(DbField.class);
			if (!(annotation instanceof DbField)) {
				continue;
			}
			// 绑定数据库的字段
			DbField dbField = (DbField) annotation;
			if (dbField.name() == null || dbField.name().isEmpty()) {
				continue;
			}
			// 修正查询字段名称
			for (ICondition condition : this.getConditions()) {
				if (!propertyInfo.getName().equalsIgnoreCase(condition.getAlias())) {
					continue;
				}
				// 修正字段名称
				condition.setAlias(dbField.name());
				// 修正类型
				condition.setAliasDataType(dbField.type());
				// 修正枚举值
				if (propertyInfo.getValueType().isEnum()) {
					Object value = null;
					if (DataConvert.isNumeric(condition.getValue())) {
						// 数字转枚举
						value = DataConvert.toEnumValue(propertyInfo.getValueType(),
								Integer.valueOf(condition.getValue()));
					} else {
						value = DataConvert.toEnumValue(propertyInfo.getValueType(), condition.getValue());
					}
					if (value != null) {
						condition.setValue(value);
					}
				}
			}
			// 修正排序的字段名称
			for (ISort sort : this.getSorts()) {
				if (!propertyInfo.getName().equalsIgnoreCase(sort.getAlias())) {
					continue;
				}
				sort.setAlias(dbField.name());
			}
		}
	}
}
