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
		return new _ApprovalFactory().create(transaction);
	}

	private static class _ApprovalFactory extends ConfigurableFactory<ApprovalProcessManager> {

		public ApprovalProcessManager create(ITransaction transaction) {
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
}
