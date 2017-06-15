package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;

/**
 * 审批工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class ApprovalFactory extends ConfigurableFactory<IApprovalProcessManager> {

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

	private IApprovalProcessManager manager = null;

	/**
	 * 创建流程管理员实例
	 * 
	 * @return
	 * @throws ApprovalException
	 */
	public synchronized IApprovalProcessManager createManager() {
		if (manager == null) {
			manager = this.create(MyConfiguration.CONFIG_ITEM_APPROVAL_WAY, "ApprovalProcessManager");
		}
		return manager;
	}

	@Override
	protected IApprovalProcessManager createDefault(String typeName) {
		return new IApprovalProcessManager() {
			@Override
			public IApprovalProcess checkProcess(IApprovalData data, IBORepository repository) {
				if (data.getApprovalStatus() != emApprovalStatus.UNAFFECTED) {
					// 重置数据状态
					data.setApprovalStatus(emApprovalStatus.UNAFFECTED);
				}
				return null;
			}
		};
	}

}
