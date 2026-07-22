package org.colorcoding.ibas.bobas.expression;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 表达式工厂（单例）
 */
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

	/**
	 * 根据值类型创建判断表达式
	 *
	 * @param type 值类型；为null时返回null
	 * @return 判断表达式实例
	 * @throws JudgmentOperationException 不支持的类型时抛出
	 */
	public IJudgmentExpression createJudgment(Class<?> type)  {
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
		} else if (type == BigDecimal.class) {
			return new JudgmentExpressionDecimal();
		} else if (type == DateTime.class) {
			return new JudgmentExpressionDateTime();
		} else if (type == Boolean.class) {
			return new JudgmentExpressionBoolean();
		} else if (type.isEnum()) {
			return new JudgmentExpressionEnum();
		}
		throw new JudgmentOperationException(I18N.prop("msg_bobas_not_support_type_expression", type.getName()));
	}

}