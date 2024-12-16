package org.colorcoding.ibas.bobas.logic;

import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.repository.ITransaction;

public class BusinessLogicChain implements IBusinessLogicChain {

	private IBusinessObject trigger;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBusinessObject> T getTrigger() {
		return (T) this.trigger;
	}

	@Override
	public <T extends IBusinessObject> void setTrigger(T trigger) {
		this.trigger = trigger;
	}

	private IBusinessObject triggerCopy;

	@SuppressWarnings("unchecked")
	protected <T extends IBusinessObject> T getTriggerCopy() {
		return (T) this.triggerCopy;
	}

	@Override
	public <T extends IBusinessObject> void setTriggerCopy(T trigger) {
		this.triggerCopy = trigger;

	}

	private ITransaction transaction;

	protected ITransaction getTransaction() {
		return this.transaction;
	}

	@Override
	public void setTransaction(ITransaction transaction) {
		Objects.requireNonNull(transaction);
		this.transaction = transaction;
	}

	@Override
	public void forwardLogics() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reverseLogics() {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

}
