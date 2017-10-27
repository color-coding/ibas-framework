package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.expression.BOJudgmentLink;
import org.colorcoding.ibas.bobas.expression.DBFieldValueOperator;
import org.colorcoding.ibas.bobas.expression.IPropertyValueOperator;
import org.colorcoding.ibas.bobas.expression.IValueOperator;
import org.colorcoding.ibas.bobas.expression.JudgmentLinkException;
import org.colorcoding.ibas.bobas.expression.JudgmentLinkItem;
import org.colorcoding.ibas.bobas.expression.JudmentOperation;
import org.colorcoding.ibas.bobas.expression.SQLScriptValueOperator;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;

public class ApprovalDataJudgmentLink extends BOJudgmentLink {

	private IBORepository4DbReadonly repository;

	public final IBORepository4DbReadonly getRepository() {
		return repository;
	}

	public final void setRepository(IBORepository4DbReadonly repository) {
		this.repository = repository;
	}

	/**
	 * 初始化判断条件
	 * 
	 * @param conditions
	 */
	public void parsingConditions(IApprovalProcessStepCondition[] conditions) {
		// 判断无条件
		if (conditions == null || conditions.length == 0) {
			return;
		}
		ArrayList<JudgmentLinkItem> jLinkItems = new ArrayList<JudgmentLinkItem>();
		for (IApprovalProcessStepCondition item : conditions) {
			JudgmentLinkItem jItem = new JudgmentLinkItem();
			if (item.getRelation() == emConditionRelationship.NONE || item.getRelation() == null) {
				jItem.setRelationship(JudmentOperation.AND);
			} else {
				jItem.setRelationship(JudmentOperation.valueOf(item.getRelation()));
			}
			jItem.setOperation(JudmentOperation.valueOf(item.getOperation()));
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
			// 设置括号
			jItem.setCloseBracket(item.getBracketClose());
			jItem.setOpenBracket(item.getBracketOpen());
			jLinkItems.add(jItem);
		}
		if (jLinkItems.size() == 0) {
			throw new JudgmentLinkException(I18N.prop("msg_bobas_invaild_judgment_link_conditions"));
		}
		super.setJudgmentItems(jLinkItems.toArray(new JudgmentLinkItem[] {}));
	}

	public IPropertyValueOperator createPropertyValueOperator(ValueMode mode) {
		if (mode == ValueMode.DB_FIELD) {
			// 使用数据库字段属性比较
			return new DBFieldValueOperator();
		} else if (mode == ValueMode.SQL_SCRIPT) {
			// 数据库脚本比较
			return new SQLScriptValueOperator(this.getRepository());
		} else {
			// 默认类属性比较
			return super.createPropertyValueOperator();
		}
	}
}
