package org.colorcoding.ibas.bobas.approval;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;

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
	public synchronized ApprovalProcessManager createManager() {
		return this.create(MyConfiguration.CONFIG_ITEM_APPROVAL_WAY, "ApprovalProcessManager");
	}

	protected ApprovalProcessManager createDefault(String typeName) {
		return new _ApprovalProcessManager() {

			@Override
			public IApprovalProcess checkProcess(IApprovalData data) {
				if (data.getApprovalStatus() != emApprovalStatus.UNAFFECTED) {
					// 重置数据状态
					data.setApprovalStatus(emApprovalStatus.UNAFFECTED);
				}
				return null;
			}
		};
	}

	private class _ApprovalProcessManager extends ApprovalProcessManager {
		@Override
		public IApprovalProcess checkProcess(IApprovalData data) {
			if (data.getApprovalStatus() != emApprovalStatus.UNAFFECTED) {
				// 重置数据状态
				data.setApprovalStatus(emApprovalStatus.UNAFFECTED);
			}
			return null;
		}

		@Override
		protected Iterator<IApprovalProcess> createApprovalProcess(String boCode) {
			return null;
		}

		@Override
		protected IApprovalProcess loadApprovalProcess(String boKey) {
			return null;
		}

	}
}
