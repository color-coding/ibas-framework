package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicChain implements IBusinessLogicChain {

	private String id;

	@Override
	public final void setGroup(String value) {
		this.id = value;
	}

	@Override
	public final String getGroup() {
		return this.id;
	}

	private IBusinessLogicsHost trigger;

	@Override
	public final IBusinessLogicsHost getTrigger() {
		return this.trigger;
	}

	@Override
	public final void setTrigger(IBusinessLogicsHost bo) {
		this.trigger = bo;
	}

	private IBORepository repository;

	protected final IBORepository getRepository() {
		return this.repository;
	}

	protected final void setRepository(IBORepository boRepository) {
		this.repository = boRepository;
	}

	@Override
	public final void useRepository(IBORepository boRepository) {
		if (this.repository != null) {
			throw new RuntimeException(I18N.prop("msg_bobas_not_supported"));
		}
		this.setRepository(boRepository);
	}

	private IBusinessLogicsHost triggerCopy;

	protected final IBusinessLogicsHost getTriggerCopy() {
		if (this.triggerCopy == null) {
			this.triggerCopy = this.fetchTriggerCopy();
		}
		return this.triggerCopy;
	}

	private final void setTriggerCopy(IBusinessLogicsHost bo) {
		this.triggerCopy = bo;
	}

	private IBusinessLogicsHost fetchTriggerCopy() {
		if (this.getTrigger() instanceof IBusinessObject) {

		}
		throw new BusinessLogicException(I18N.prop("msg_bobas_not_found_bo_copy", this.getTrigger()));
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
