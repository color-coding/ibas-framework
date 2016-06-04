package org.colorcoding.ibas.bobas.expressions;

import org.colorcoding.ibas.bobas.util.StringBuilder;

/**
 * 判断链项目
 * 
 * @author Niuren.Zhu
 *
 */
public class JudgmentLinkItem {
	private JudmentOperations relationship;

	public final JudmentOperations getRelationship() {
		return relationship;
	}

	public final void setRelationship(JudmentOperations operation) {
		this.relationship = operation;
	}

	private int openBracket = 0;

	public final int getOpenBracket() {
		return openBracket;
	}

	public final void setOpenBracket(int openBracket) {
		this.openBracket = openBracket;
	}

	private IValueOperter leftOperter;

	public final IValueOperter getLeftOperter() {
		return leftOperter;
	}

	public final void setLeftOperter(IValueOperter leftOperter) {
		this.leftOperter = leftOperter;
	}

	private JudmentOperations operation;

	public final JudmentOperations getOperation() {
		return operation;
	}

	public final void setOperation(JudmentOperations operation) {
		this.operation = operation;
	}

	private IValueOperter rightOperter;

	public final IValueOperter getRightOperter() {
		return rightOperter;
	}

	public final void setRightOperter(IValueOperter rightOperter) {
		this.rightOperter = rightOperter;
	}

	private int closeBracket = 0;

	public final int getCloseBracket() {
		return closeBracket;
	}

	public final void setCloseBracket(int closeBracket) {
		this.closeBracket = closeBracket;
	}

	public String print() {
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
