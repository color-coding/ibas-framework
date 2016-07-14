package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsChain implements IBusinessLogicsChain {

	private String id;

	@Override
	public void setId(String value) {
		this.id = value;
	}

	@Override
	public String getId() {
		return this.id;
	}

	private IBORepository repository;

	protected IBORepository getRepository() {
		return this.repository;
	}

	@Override
	public void useRepository(IBORepository boRepository) {
		if (this.repository != null) {
			throw new RuntimeException(i18n.prop("msg_bobas_not_supported"));
		}
		this.repository = boRepository;
	}

	@Override
	public void run(IBusinessObjectBase bo) {
		if (bo == null) {
			return;
		}
		RuntimeLog.log(RuntimeLog.MSG_LOGICS_CHAIN_RUN, this.getId(), bo.toString());

	}

}
