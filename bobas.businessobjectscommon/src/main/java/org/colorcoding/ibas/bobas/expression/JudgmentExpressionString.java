package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DataConvert;

/**
 * 字符串值表达式比较
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentExpressionString extends JudgmentExpressionComparable<String> {
	public JudgmentExpressionString() {

	}

	public JudgmentExpressionString(String leftValue, JudgmentOperation operation, String rightValue) {
		super(leftValue, operation, rightValue);
	}

	private String leftValue;

	@Override
	public final String getLeftValue() {
		return this.leftValue;
	}

	@Override
	public final void setLeftValue(Object value) {
		this.setLeftValue(Strings.valueOf(value));
	}

	public final void setLeftValue(String value) {
		this.leftValue = value;
	}

	private String rightValue;

	@Override
	public final String getRightValue() {
		return this.rightValue;
	}

	@Override
	public final void setRightValue(Object value) {
		this.setRightValue(Strings.valueOf(value));
	}

	public final void setRightValue(String value) {
		this.rightValue = value;
	}

	@Override
	public boolean result()  {
		// 开始与
		if (this.getOperation() == JudgmentOperation.BEGIN_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue().startsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 结束于
		else if (this.getOperation() == JudgmentOperation.END_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue().endsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 非开始于
		else if (this.getOperation() == JudgmentOperation.NOT_BEGIN_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return true;
			}
			if (!this.getLeftValue().startsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 非结束于
		else if (this.getOperation() == JudgmentOperation.NOT_END_WITH) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return true;
			}
			if (!this.getLeftValue().endsWith(this.getRightValue())) {
				return true;
			}
			return false;
		}
		// 包含
		else if (this.getOperation() == JudgmentOperation.CONTAIN) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			if (this.getLeftValue().indexOf(this.getRightValue()) >= 0) {
				return true;
			}
			return false;
		}
		// 不包含
		else if (this.getOperation() == JudgmentOperation.NOT_CONTAIN) {
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return true;
			}
			if (this.getLeftValue().indexOf(this.getRightValue()) < 0) {
				return true;
			}
			return false;
		}
		// 包含
		if (this.getOperation() == JudgmentOperation.IN) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			String leftValue = Strings.makeEndsWith(this.getLeftValue(), DataConvert.DATA_SEPARATOR);
			String rightValue = Strings.makeEndsWith(this.getRightValue(), DataConvert.DATA_SEPARATOR);
			// 右值包含左值
			if (rightValue.indexOf(leftValue) >= 0) {
				return true;
			}
			return false;
		}
		// 不包含
		else if (this.getOperation() == JudgmentOperation.NOT_IN) {
			// 左值为空或右值为空
			if (this.getLeftValue() == null || this.getRightValue() == null) {
				return false;
			}
			String leftValue = Strings.makeEndsWith(this.getLeftValue(), DataConvert.DATA_SEPARATOR);
			String rightValue = Strings.makeEndsWith(this.getRightValue(), DataConvert.DATA_SEPARATOR);
			// 右值包含左值
			if (rightValue.indexOf(leftValue) < 0) {
				return true;
			}
			return false;
		}
		// 其他
		return super.result();
	}
}
