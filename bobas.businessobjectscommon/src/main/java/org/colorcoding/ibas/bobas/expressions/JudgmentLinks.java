package org.colorcoding.ibas.bobas.expressions;

import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.messages.Logger;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.util.ArrayList;
import org.colorcoding.ibas.bobas.util.StringBuilder;

public abstract class JudgmentLinks {

	protected static final String MSG_JUDGMENT_EXPRESSION = "judgment: expression %s = [%s]";
	protected static final String MSG_JUDGMENT_RELATION = "judgment: relation %s = [%s]";
	protected static final String MSG_JUDGMENT_LINK_INFO = "judgment: judgment item count [%s].";
	protected static final String MSG_JUDGMENT_ENTRY_SUB_JUDGMENT = "judgment: entry sub judgment [%s].";
	protected static final String MSG_JUDGMENT_NOT_FOUND_PROPERTY = "judgment: not found [%s]'s property [%s].";
	protected static final String MSG_JUDGMENT_PROPERTY_NULL = "judgment: [%s].[%s] value is null.";

	private JudgmentLinkItem[] judgmentItems;

	/**
	 * 获取判断链项目
	 * 
	 * @return
	 */
	public final JudgmentLinkItem[] getJudgmentItems() {
		return judgmentItems;
	}

	/**
	 * 设置判断链项目
	 * 
	 * @param judgmentItems
	 */
	public final void setJudgmentItems(JudgmentLinkItem[] judgmentItems) {
		this.judgmentItems = judgmentItems;
	}

	/**
	 * 创建值操作
	 * 
	 * @return
	 */
	protected IValueOperator createValueOperator() {
		return new IValueOperator() {
			private Object value;

			@Override
			public void setValue(Object value) {
				this.value = value;
			}

			@Override
			public Object getValue() {
				return this.value;
			}

			@Override
			public Class<?> getValueClass() {
				if (this.value == null) {
					return null;
				}
				return this.value.getClass();
			}

			@Override
			public String toString() {
				return String.format("{value: %s}", this.getValue());
			}
		};
	}

	/**
	 * 判断对象是否满足条件
	 * 
	 * @param object
	 *            对象
	 * @return true,满足;false,不满足
	 * @throws JudmentOperationException
	 */
	public boolean judge(Object object) throws JudmentOperationException {
		// 无条件
		if (this.getJudgmentItems() == null) {
			return true;
		}
		// 设置所以条件的比较值
		for (JudgmentLinkItem item : this.getJudgmentItems()) {
			// 左值
			if (item.getLeftOperter() instanceof IPropertyValueOperator) {
				IPropertyValueOperator pOperter = (IPropertyValueOperator) item.getLeftOperter();
				pOperter.setValue(object);
			}
			// 右值
			if (item.getRightOperter() instanceof IPropertyValueOperator) {
				IPropertyValueOperator pOperter = (IPropertyValueOperator) item.getRightOperter();
				pOperter.setValue(object);
			}
		}
		return this.judge(0, this.getJudgmentItems());
	}

	protected void log(JudgmentLinkItem[] judgmentItems) {
		if (judgmentItems == null) {
			return;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.appendFormat(MSG_JUDGMENT_LINK_INFO, judgmentItems.length);
		String line = System.getProperty("line.seperator", "\n");
		for (JudgmentLinkItem jItem : judgmentItems) {
			stringBuilder.append(line);
			stringBuilder.append(" ");
			stringBuilder.append(jItem.toString());
		}
		Logger.log(MessageLevel.DEBUG, stringBuilder.toString());
	}

	/**
	 * 获取括号内的判断条件
	 * 
	 * @param bracket
	 *            括号
	 * @param judgmentItems
	 *            条件
	 * @param startIndex
	 *            开始的索引
	 * @return
	 */
	private JudgmentLinkItem[] getJudgmentItems(int startIndex, JudgmentLinkItem[] judgmentItems) {
		boolean done = false;// 完成
		int closeCount = 0;
		int bracket = -1;
		ArrayList<JudgmentLinkItem> currentJudgmentItems = new ArrayList<JudgmentLinkItem>();
		for (int i = startIndex; i < judgmentItems.length; i++) {
			JudgmentLinkItem jItem = judgmentItems[i];
			if (bracket == -1) {
				bracket = jItem.getOpenBracket();
			}
			currentJudgmentItems.add(jItem);
			if (jItem.getCloseBracket() > 0) {
				closeCount += jItem.getCloseBracket();
			}
			if (jItem.getOpenBracket() > 0 && i != startIndex) {
				closeCount -= jItem.getOpenBracket();
			}
			if (closeCount >= bracket) {
				// 闭环
				done = true;
				break;
			}
		}
		if (!done) {
			// 未标记完成，存在不匹配的括号
			throw new JudgmentLinksException(I18N.prop("msg_bobas_invaild_judgment_link_bracket", bracket));
		}
		JudgmentLinkItem[] jItems = currentJudgmentItems.toArray(new JudgmentLinkItem[] {});
		return jItems;
	}

	/**
	 * 判断条件是否成立
	 * 
	 * @param bracket
	 *            当前括号数
	 * @param judgmentItems
	 *            当前判断条件
	 * @return true,满足;false,不满足
	 * @throws JudmentOperationException
	 */
	protected boolean judge(int bracket, JudgmentLinkItem[] judgmentItems) throws JudmentOperationException {
		this.log(judgmentItems);
		boolean currentValue = false;// 当前的结果
		ExpressionFactory factory = ExpressionFactory.create();
		IJudgmentExpression rootJudExp = null;
		for (int i = 0; i < judgmentItems.length; i++) {
			JudgmentLinkItem jItem = judgmentItems[i];
			if (rootJudExp != null) {
				rootJudExp.setOperation(jItem.getRelationship());
			}
			// 计算表达式结果
			if ((jItem.getOpenBracket() != bracket || (jItem.getOpenBracket() == bracket && i > 0))
					&& jItem.getOpenBracket() > 0) {
				// 新的括号，先执行新括号判断
				JudgmentLinkItem[] nextJudgmentItems = this.getJudgmentItems(i, judgmentItems);
				currentValue = this.judge(jItem.getOpenBracket(), nextJudgmentItems);
				// 跳过已执行的
				if (nextJudgmentItems.length > 0) {
					i = i + nextJudgmentItems.length - 1;
				}
			} else {
				// 计算当前表达式
				IJudgmentExpression currentJudExp = factory.createJudgment(jItem.getLeftOperter().getValueClass());
				currentJudExp.setLeftValue(jItem.getLeftOperter().getValue());
				currentJudExp.setOperation(jItem.getOperation());
				currentJudExp.setRightValue(jItem.getRightOperter().getValue());
				currentValue = currentJudExp.result();
				Logger.log(MessageLevel.DEBUG, MSG_JUDGMENT_EXPRESSION, currentJudExp.toString(), currentValue);
			}
			if (rootJudExp == null) {
				// 第一个表达式
				rootJudExp = factory.createJudgment(Boolean.class);
				rootJudExp.setLeftValue(currentValue);
				rootJudExp.setOperation(JudmentOperations.AND);
				rootJudExp.setRightValue(true);
			} else {
				// 后续表达式
				rootJudExp.setRightValue(currentValue);
			}
			currentValue = rootJudExp.result();
			Logger.log(MessageLevel.DEBUG, MSG_JUDGMENT_RELATION, rootJudExp.toString(), currentValue);
			rootJudExp.setLeftValue(currentValue);// 结果左移
			if (!rootJudExp.result()) {
				// 表达式不成立
				if (i == judgmentItems.length - 1) {
					// 最后一个表达式，返回结果
					return false;
				}
			}
		}
		return true;
	}

}
