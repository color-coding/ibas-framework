package org.colorcoding.ibas.bobas.logics;

import java.util.HashMap;
import java.util.UUID;

/**
 * 业务逻辑管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsManager implements IBusinessLogicsManager {

	@Override
	public IBusinessLogicsChain getChain(String transId) {
		if (transId != null) {
			if (this.getLogicsChains().containsKey(transId)) {
				return this.getLogicsChains().get(transId);
			}
		}
		return null;
	}

	@Override
	public IBusinessLogicsChain createChain(String transId) {
		IBusinessLogicsChain chain = new BusinessLogicsChain();
		if (transId == null || transId.equals("")) {
			transId = UUID.randomUUID().toString();
		}
		chain.setId(transId);
		this.getLogicsChains().put(chain.getId(), chain);
		return chain;
	}

	private volatile HashMap<String, IBusinessLogicsChain> logicsChains;

	protected HashMap<String, IBusinessLogicsChain> getLogicsChains() {
		if (this.logicsChains == null) {
			synchronized (BusinessLogicsManager.class) {
				if (this.logicsChains == null) {
					this.logicsChains = new HashMap<String, IBusinessLogicsChain>();
				}
			}
		}
		return this.logicsChains;
	}

}
