package org.colorcoding.ibas.bobas.approval;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.repository.ITransaction;

/**
 * 审批工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class ApprovalFactory extends ConfigurableFactory<ApprovalProcessManager> {

	private ApprovalFactory() {
	}

	private volatile static ApprovalFactory instance;

	public synchronized static ApprovalFactory create() {
		if (instance == null) {
			synchronized (ApprovalFactory.class) {
				if (instance == null) {
					instance = new ApprovalFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * 创建流程管理员实例
	 * 
	 * @return
	 * @throws ApprovalException
	 */
	public synchronized ApprovalProcessManager createManager(ITransaction transaction) {
		ApprovalProcessManager manager = this.create(MyConfiguration.CONFIG_ITEM_APPROVAL_WAY,
				"ApprovalProcessManager");
		manager.setTransaction(transaction);
		return manager;
	}

	@Override
	protected ApprovalProcessManager createDefault(String typeName) {
		return new ApprovalProcessManager() {

			@Override
			public <T extends IApprovalProcess> Iterator<ApprovalProcess<T>> createApprovalProcess(String boCode) {
				return null;
			}

			@Override
			public <T extends IApprovalProcess> ApprovalProcess<T> createApprovalProcess(
					IApprovalProcess processData) {
				return null;
			}

			@Override
			public <T extends IApprovalProcess> T loadProcessData(IApprovalData apData) {
				return null;
			}

		};
	}

}
