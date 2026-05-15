package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;
import org.colorcoding.ibas.bobas.db.DbTransaction;
import org.colorcoding.ibas.bobas.expression.BOJudgmentLink;
import org.colorcoding.ibas.bobas.expression.DBFieldValueOperator;
import org.colorcoding.ibas.bobas.expression.ExpressionException;
import org.colorcoding.ibas.bobas.expression.IPropertyValueOperator;
import org.colorcoding.ibas.bobas.expression.IValueOperator;
import org.colorcoding.ibas.bobas.expression.JudgmentLinkItem;
import org.colorcoding.ibas.bobas.expression.JudmentOperation;
import org.colorcoding.ibas.bobas.expression.SQLScriptValueOperator;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.ITransaction;

/**
 * 审批数据条件判断链，将审批步骤条件转换为判断链
 */
public class ApprovalDataJudgmentLink extends BOJudgmentLink {

	public ApprovalDataJudgmentLink(ITransaction transaction) {
		this.transaction = transaction;
	}

	private ITransaction transaction;

	/**
	 * 解析审批步骤条件，转换为判断链项
	 *
	 * @param conditions 审批步骤条件数组，为空或null时不做处理
	 * @throws ExpressionException 条件无效时抛出
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
				valueOperator.setPropertyName(item.getPropertyName());
				jItem.setRightOperter(valueOperator);
			}
			// 设置括号
			jItem.setCloseBracket(item.getBracketClose());
			jItem.setOpenBracket(item.getBracketOpen());
			jLinkItems.add(jItem);
		}
		if (jLinkItems.isEmpty()) {
			throw new ExpressionException(I18N.prop("msg_bobas_invaild_judgment_link_conditions"));
		}
		super.setJudgmentItems(jLinkItems.toArray(new JudgmentLinkItem[jLinkItems.size()]));
	}

	/**
	 * 根据取值方式创建属性值操作器
	 *
	 * @param mode 取值方式：PROPERTY使用对象属性，DB_FIELD使用数据库字段，SQL_SCRIPT使用数据库脚本
	 * @return 属性值操作器
	 * @throws ExpressionException SQL_SCRIPT模式要求DbTransaction类型的事务
	 */
	public IPropertyValueOperator createPropertyValueOperator(ValueMode mode) {
		if (mode == ValueMode.DB_FIELD) {
			// 使用数据库字段属性比较
			return new DBFieldValueOperator();
		} else if (mode == ValueMode.SQL_SCRIPT) {
			// 数据库脚本比较
			if (!(this.transaction instanceof DbTransaction)) {
				throw new ExpressionException(I18N.prop("msg_bobas_invaild_database_connection"));
			}
			return new SQLScriptValueOperator((DbTransaction) this.transaction);
		} else {
			// 默认类属性比较
			return super.createPropertyValueOperator();
		}
	}
}