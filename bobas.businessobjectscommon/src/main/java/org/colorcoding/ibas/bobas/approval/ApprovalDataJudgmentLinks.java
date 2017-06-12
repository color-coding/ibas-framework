package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.expressions.BOJudgmentLinks;
import org.colorcoding.ibas.bobas.expressions.IPropertyValueOperator;
import org.colorcoding.ibas.bobas.expressions.IValueOperator;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinkItem;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinksException;
import org.colorcoding.ibas.bobas.expressions.JudmentOperations;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.util.ArrayList;

public class ApprovalDataJudgmentLinks extends BOJudgmentLinks {
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
			IPropertyValueOperator propertyValueOperator = this
					.createPropertyValueOperator(item.getPropertyValueMode());
			propertyValueOperator.setPropertyName(item.getPropertyName());
			jItem.setLeftOperter(propertyValueOperator);
			// 右边取值
			if (item.getConditionValueMode() == ValueMode.INPUT) {
				// 与值比较
				IValueOperator valueOperator = this.createValueOperator();
				valueOperator.setValue(item.getConditionValue());
				jItem.setRightOperter(valueOperator);
			} else {
				// 与对象属性值比较
				IPropertyValueOperator valueOperator = this.createPropertyValueOperator(item.getConditionValueMode());
				valueOperator.setPropertyName(item.getConditionValue());
				jItem.setRightOperter(valueOperator);
			}
			jLinkItems.add(jItem);
		}
		if (jLinkItems.size() == 0) {
			throw new JudgmentLinksException(i18n.prop("msg_bobas_invaild_judgment_link_conditions"));
		}
		super.setJudgmentItems(jLinkItems.toArray(new JudgmentLinkItem[] {}));
	}

	public IPropertyValueOperator createPropertyValueOperator(ValueMode mode) {
		if (mode == ValueMode.DB_FIELD) {
			// 使用数据库字段属性比较
			return new DBFieldValueOperator();
		} else {
			// 默认类属性比较
			return super.createPropertyValueOperator();
		}
	}
}
