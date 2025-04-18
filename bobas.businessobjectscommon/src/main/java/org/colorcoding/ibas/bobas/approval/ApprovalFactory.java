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
public class ApprovalFactory {

	private ApprovalFactory() {
	}

	public synchronized static ApprovalProcessManager createManager(ITransaction transaction) {
		return new Factory().create(transaction);
	}

	private static class Factory extends ConfigurableFactory<ApprovalProcessManager> {

		public ApprovalProcessManager create(ITransaction transaction) {
			ApprovalProcessManager manager = this.create(MyConfiguration.CONFIG_ITEM_APPROVAL_WAY,
					ApprovalProcessManager.class.getSimpleName());
			manager.setTransaction(transaction);
			return manager;
		}

		@Override
		protected ApprovalProcessManager createDefault(String typeName) {
			return new ApprovalProcessManager() {

				@Override
				public IProcessData loadProcessData(IApprovalData apData) {
					return null;
				}

				@Override
				protected ApprovalProcess<IProcessData> createApprovalProcess(IProcessData data) {
					return null;
				}

				@Override
				protected Iterator<ApprovalProcess<IProcessData>> createApprovalProcess(String boCode) {
					return null;
				}

			};
		}
	}
}
