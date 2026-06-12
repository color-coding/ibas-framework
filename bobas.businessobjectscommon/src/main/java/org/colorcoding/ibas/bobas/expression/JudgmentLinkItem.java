package org.colorcoding.ibas.bobas.expression;

/**
 * 判断链项目
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentLinkItem {
	private JudgmentOperation relationship;

	public final JudgmentOperation getRelationship() {
		return relationship;
	}

	public final void setRelationship(JudgmentOperation operation) {
		this.relationship = operation;
	}

	private int openBracket = 0;

	public final int getOpenBracket() {
		return openBracket;
	}

	public final void setOpenBracket(int openBracket) {
		this.openBracket = openBracket;
	}

	private IValueOperator leftOperator;

	public final IValueOperator getLeftOperator() {
		return leftOperator;
	}

	public final void setLeftOperator(IValueOperator leftOperator) {
		this.leftOperator = leftOperator;
	}

	private JudgmentOperation operation;

	public final JudgmentOperation getOperation() {
		return operation;
	}

	public final void setOperation(JudgmentOperation operation) {
		this.operation = operation;
	}

	private IValueOperator rightOperator;

	public final IValueOperator getRightOperator() {
		return rightOperator;
	}

	public final void setRightOperator(IValueOperator rightOperator) {
		this.rightOperator = rightOperator;
	}

	private int closeBracket = 0;

	public final int getCloseBracket() {
		return closeBracket;
	}

	public final void setCloseBracket(int closeBracket) {
		this.closeBracket = closeBracket;
	}

	@Override
	public String toString() {
		// AND ((ItemCode = "A" OR Customer = "C") AND Total >= 1000)
		StringBuilder stringBuilder = new StringBuilder(64);
		stringBuilder.append(this.getRelationship().toString());
		stringBuilder.append(" ");
		for (int i = 0; i < this.getOpenBracket(); i++) {
			stringBuilder.append("(");
		}
		stringBuilder.append(this.getLeftOperator().toString());
		stringBuilder.append(" ");
		stringBuilder.append(this.getOperation().toString());
		stringBuilder.append(" ");
		stringBuilder.append(this.getRightOperator().toString());
		for (int i = 0; i < this.getCloseBracket(); i++) {
			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}

}
