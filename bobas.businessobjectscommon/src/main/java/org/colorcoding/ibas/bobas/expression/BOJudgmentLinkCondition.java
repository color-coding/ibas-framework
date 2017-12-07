package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 业务对象判断链
 * 
 * @author Niuren.Zhu
 *
 */
public class BOJudgmentLinkCondition extends BOJudgmentLink {
	/**
	 * 初始化判断条件
	 * 
	 * @param conditions
	 */
	public void parsingConditions(Iterable<ICondition> conditions) {
		// 判断无条件
		if (conditions == null || conditions.toString().equals("[]")) {
			return;
		}
		ArrayList<JudgmentLinkItem> jLinkItems = new ArrayList<JudgmentLinkItem>();
		for (ICondition item : conditions) {
			JudgmentLinkItem jItem = new JudgmentLinkItem();
			jItem.setOpenBracket(item.getBracketOpen());
			jItem.setCloseBracket(item.getBracketClose());
			if (item.getRelationship() == ConditionRelationship.NONE) {
				jItem.setRelationship(JudmentOperation.AND);
			} else {
				jItem.setRelationship(JudmentOperation.valueOf(item.getRelationship()));
			}
			jItem.setOperation(JudmentOperation.valueOf(item.getOperation()));
			// 左边取值
			IPropertyValueOperator propertyValueOperator = this.createPropertyValueOperator();
			propertyValueOperator.setPropertyName(item.getAlias());
			jItem.setLeftOperter(propertyValueOperator);
			// 右边取值
			if (item.getComparedAlias() != null && !item.getComparedAlias().isEmpty()) {
				// 与属性比较
				propertyValueOperator = this.createPropertyValueOperator();
				propertyValueOperator.setPropertyName(item.getComparedAlias());
				jItem.setRightOperter(propertyValueOperator);
			} else {
				// 与值比较
				IValueOperator ValueOperator = this.createValueOperator();
				ValueOperator.setValue(item.getValue());
				jItem.setRightOperter(ValueOperator);
			}
			jLinkItems.add(jItem);
		}
		if (jLinkItems.isEmpty()) {
			throw new JudgmentLinkException(I18N.prop("msg_bobas_invaild_judgment_link_conditions"));
		}
		super.setJudgmentItems(jLinkItems.toArray(new JudgmentLinkItem[] {}));
	}

}
