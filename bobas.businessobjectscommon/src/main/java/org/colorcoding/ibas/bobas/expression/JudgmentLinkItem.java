package org.colorcoding.ibas.bobas.expression;

/**
 * 判断链项目
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentLinkItem {
	private JudmentOperation relationship;

	public final JudmentOperation getRelationship() {
		return relationship;
	}

	public final void setRelationship(JudmentOperation operation) {
		this.relationship = operation;
	}

	private int openBracket = 0;

	public final int getOpenBracket() {
		return openBracket;
	}

	public final void setOpenBracket(int openBracket) {
		this.openBracket = openBracket;
	}

	private IValueOperator leftOperter;

	public final IValueOperator getLeftOperter() {
		return leftOperter;
	}

	public final void setLeftOperter(IValueOperator leftOperter) {
		this.leftOperter = leftOperter;
	}

	private JudmentOperation operation;

	public final JudmentOperation getOperation() {
		return operation;
	}

	public final void setOperation(JudmentOperation operation) {
		this.operation = operation;
	}

	private IValueOperator rightOperter;

	public final IValueOperator getRightOperter() {
		return rightOperter;
	}

	public final void setRightOperter(IValueOperator rightOperter) {
		this.rightOperter = rightOperter;
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
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getRelationship().toString());
		stringBuilder.append(" ");
		for (int i = 0; i < this.getOpenBracket(); i++) {
			stringBuilder.append("(");
		}
		stringBuilder.append(this.getLeftOperter().toString());
		stringBuilder.append(" ");
		stringBuilder.append(this.getOperation().toString());
		stringBuilder.append(" ");
		stringBuilder.append(this.getRightOperter().toString());
		for (int i = 0; i < this.getCloseBracket(); i++) {
			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}

}
