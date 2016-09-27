package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.expressions.BOJudgmentLinks;
import org.colorcoding.ibas.bobas.expressions.IPropertyValueOperter;
import org.colorcoding.ibas.bobas.expressions.IValueOperter;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinkItem;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinksException;
import org.colorcoding.ibas.bobas.expressions.JudmentOperations;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.util.ArrayList;

public class ApprovalDataJudgmentLinks extends BOJudgmentLinks {
	/**
	 * 数据比较，属性比较
	 */
	public final static String PROPERTY_VALUE_OPERTER_PROPERTY = "Property";
	/**
	 * 数据比较，数据库字段比较
	 */
	public final static String PROPERTY_VALUE_OPERTER_DB_FIELD = "DbField";

	/**
	 * 初始化判断条件
	 * 
	 * @param conditions
	 */
	public void parsingConditions(IApprovalProcessStepCondition[] conditions) {
		// 判断无条件
		if (conditions != null && conditions.length == 0) {
			return;
		}
		ArrayList<JudgmentLinkItem> jLinkItems = new ArrayList<JudgmentLinkItem>();
		for (IApprovalProcessStepCondition item : conditions) {
			JudgmentLinkItem jItem = new JudgmentLinkItem();
			jItem.setOpenBracket(0);
			jItem.setCloseBracket(0);
			if (item.getRelation() == emConditionRelationship.NONE || item.getRelation() == null) {
				jItem.setRelationship(JudmentOperations.AND);
			} else {
				jItem.setRelationship(JudmentOperations.valueOf(item.getRelation()));
			}
			jItem.setOperation(JudmentOperations.valueOf(item.getOperation()));
			// 左边取值
			IPropertyValueOperter propertyValueOperter = this.createPropertyValueOperter();
			propertyValueOperter.setPropertyName(item.getPropertyName());
			jItem.setLeftOperter(propertyValueOperter);
			// 右边取值
			// 与值比较
			IValueOperter valueOperter = this.createValueOperter();
			valueOperter.setValue(item.getConditionValue());
			jItem.setRightOperter(valueOperter);
			jLinkItems.add(jItem);
		}
		if (jLinkItems.size() == 0) {
			throw new JudgmentLinksException(i18n.prop("msg_bobas_invaild_judgment_link_conditions"));
		}
		super.setJudgmentItems(jLinkItems.toArray(new JudgmentLinkItem[] {}));
	}

	@Override
	public IPropertyValueOperter createPropertyValueOperter() {
		if (PROPERTY_VALUE_OPERTER_DB_FIELD.equalsIgnoreCase(MyConfiguration.getConfigValue(
				MyConfiguration.CONFIG_ITEM_APPROVAL_DATA_JUDGMENT_VALUE_WAY, PROPERTY_VALUE_OPERTER_DB_FIELD))) {
			// 使用数据库字段属性比较
			return new IPropertyValueOperter() {
				private IManageFields value;
				private IFieldData field = null;

				private IFieldData getField() {
					if (this.field == null) {
						for (IFieldData item : value.getFields()) {
							if (item instanceof IFieldDataDb) {
								IFieldDataDb dbField = (IFieldDataDb) item;
								if (dbField.getDbField().equalsIgnoreCase(this.getPropertyName())) {
									this.field = dbField;
									break;
								}
							}
						}
					}
					if (this.field == null) {
						throw new JudgmentLinksException(
								i18n.prop("msg_bobas_not_found_bo_field", this.getPropertyName()));
					}
					return this.field;
				}

				@Override
				public void setValue(Object value) {
					if (value != null && !(value instanceof IManageFields)) {
						throw new JudgmentLinksException(i18n.prop("msg_bobas_invaild_bo_type"));
					}
					this.value = (IManageFields) value;
					this.field = null;
				}

				@Override
				public Object getValue() {
					if (this.value == null) {
						return null;
					}
					return this.getField().getValue();
				}

				@Override
				public Class<?> getValueClass() {
					if (this.value == null) {
						return null;
					}
					return this.getField().getValueType();
				}

				private String propertyName;

				@Override
				public void setPropertyName(String value) {
					this.propertyName = value;
				}

				@Override
				public String getPropertyName() {
					return this.propertyName;
				}

				private static final String format_template = "{property's value: %s}";

				@Override
				public String toString() {
					return String.format(format_template, this.getPropertyName());
				}
			};
		}
		// 默认类属性比较
		return super.createPropertyValueOperter();
	}

}
