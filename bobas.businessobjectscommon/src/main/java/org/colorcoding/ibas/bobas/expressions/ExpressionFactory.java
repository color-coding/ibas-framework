package org.colorcoding.ibas.bobas.expressions;

import org.colorcoding.ibas.bobas.common.IConditions;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.i18n;

public class ExpressionFactory {

	private ExpressionFactory() {
	}

	private volatile static ExpressionFactory instance;

	public static ExpressionFactory create() {
		if (instance == null) {
			synchronized (ExpressionFactory.class) {
				if (instance == null) {
					instance = new ExpressionFactory();
				}
			}
		}
		return instance;
	}

	public IJudgmentExpression createJudgment(Class<?> type) throws JudmentOperationException {
		if (type == null) {
			return null;
		}
		if (type == String.class) {
			return new JudgmentExpressionString();
		} else if (type == Integer.class) {
			return new JudgmentExpressionInteger();
		} else if (type == Long.class) {
			return new JudgmentExpressionLong();
		} else if (type == Double.class) {
			return new JudgmentExpressionDouble();
		} else if (type == Float.class) {
			return new JudgmentExpressionFloat();
		} else if (type == Short.class) {
			return new JudgmentExpressionShort();
		} else if (type == Decimal.class) {
			return new JudgmentExpressionDecimal();
		} else if (type == DateTime.class) {
			return new JudgmentExpressionDateTime();
		} else if (type == Boolean.class) {
			return new JudgmentExpressionBoolean();
		} else if (type.isEnum()) {
			return new JudgmentExpressionEnum();
		}
		throw new JudmentOperationException(i18n.prop("msg_bobas_not_support_type_expression", type.getName()));
	}

	/**
	 * 创建业务对象判断链
	 * 
	 * @param conditions
	 *            条件
	 * @return
	 */
	public JudgmentLinks createBOJudgmentLinks(IConditions conditions) {
		BOJudgmentLinksCondition judgmentLinks = new BOJudgmentLinksCondition();
		judgmentLinks.parsingConditions(conditions);
		return judgmentLinks;
	}

	/**
	 * 创建文件判断链
	 * 
	 * @param conditions
	 *            条件
	 * @return
	 */
	public JudgmentLinks createFileJudgmentLinks(IConditions conditions) {
		FileJudgmentLinks judgmentLinks = new FileJudgmentLinks();
		judgmentLinks.parsingConditions(conditions);
		return judgmentLinks;
	}
}
