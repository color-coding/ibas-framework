package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 枚举值表达式比较
 *
 * 等于/不等时同时比较枚举名称和注解值
 */
public class JudgmentExpressionEnum extends JudgmentExpression<Object> {

	private Object leftValue;

	@Override
	public Object getLeftValue() {
		return this.leftValue;
	}

	@Override
	public void setLeftValue(Object value) {
		this.leftValue = value;
	}

	private Object rightValue;

	@Override
	public Object getRightValue() {
		return this.rightValue;
	}

	@Override
	public void setRightValue(Object value) {
		this.rightValue = value;
	}

	@Override
	public boolean result() throws ExpressionException {
		// 等
		if (this.getOperation() == JudgmentOperation.EQUAL) {
			String tmpLeft = Strings.valueOf(this.getLeftValue());
			String tmpLeftDb = Enums.annotationValue(this.getLeftValue());
			String tmpRight = Strings.valueOf(this.getRightValue());
			String tmpRightDb = Enums.annotationValue(this.getRightValue());
			if (tmpRightDb == null && this.getRightValue() != null) {
				// 按枚举取值无效，则使用初值
				tmpRightDb = Strings.valueOf(this.getRightValue());
			}
			if (tmpLeft != null && tmpLeft.equals(tmpRight)) {
				return true;
			}
			if (tmpLeftDb != null && tmpLeftDb.equals(tmpRightDb)) {
				return true;
			}
			return false;
		}
		// 不等
		else if (this.getOperation() == JudgmentOperation.NOT_EQUAL) {
			String tmpLeft = Strings.valueOf(this.getLeftValue());
			String tmpLeftDb = Enums.annotationValue(this.getLeftValue());
			String tmpRight = Strings.valueOf(this.getRightValue());
			String tmpRightDb = Enums.annotationValue(this.getRightValue());
			if (tmpRightDb == null && this.getRightValue() != null) {
				// 按枚举取值无效，则使用初值
				tmpRightDb = Strings.valueOf(this.getRightValue());
			}
			if (tmpLeft != null && tmpLeft.equals(tmpRight)) {
				return false;
			}
			if (tmpLeftDb != null && tmpLeftDb.equals(tmpRightDb)) {
				return false;
			}
			return true;
		}
		return super.result();
	}
}
