package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 业务逻辑
 * 
 * @author Niuren.Zhu
 *
 * @param <L>
 *            业务逻辑的契约
 */
public class BusinessLogic<L extends IBusinessLogicContract> implements IBusinessLogic {

	private L contract;

	/**
	 * 获取-契约数据
	 * 
	 * @return
	 */
	public L getContract() {
		return this.contract;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContract(IBusinessLogicContract contract) {
		this.contract = (L) contract;
	}

	private IBORepository repository;

	/**
	 * 获取-仓库
	 * 
	 * @return
	 */
	protected IBORepository getRepository() {
		return this.repository;
	}

	@Override
	public void setRepository(IBORepository repository) {
		this.repository = repository;
	}

	@Override
	public void forward() {
		RuntimeLog.log(RuntimeLog.MSG_LOGICS_RUNNING_LOGIC_FORWARD, this.getClass().getName(),
				this.getContract().toString());

	}

	@Override
	public void reverse() {
		RuntimeLog.log(RuntimeLog.MSG_LOGICS_RUNNING_LOGIC_REVERSE, this.getClass().getName(),
				this.getContract().toString());

	}

}
