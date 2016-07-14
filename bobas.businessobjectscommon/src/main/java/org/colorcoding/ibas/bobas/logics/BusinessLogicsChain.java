package org.colorcoding.ibas.bobas.logics;

import java.util.HashMap;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

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

	/**
	 * 分析数据，获取契约
	 * 
	 * @param bo
	 *            数据
	 * @return 具有的契约
	 */
	protected IBusinessLogic[] analyzeContracts(IBusinessObjectBase bo) {
		ArrayList<IBusinessLogic> contracts = new ArrayList<>();
		if (bo == null) {
			return contracts.toArray(new IBusinessLogic[] {});
		}
		// 先子项，再自身
		// 注意：避免嵌套后无限循环寻找契约
		if (bo instanceof IManageFields) {
			IManageFields boFields = (IManageFields) bo;
			for (IFieldData item : boFields.getFields()) {
				if (item.getValue() instanceof IBusinessObjectBase) {
					contracts.addAll(this.analyzeContracts((IBusinessObjectBase) item.getValue()));
				} else if (item.getValue() instanceof IBusinessObjectListBase) {
					IBusinessObjectListBase<?> bos = (IBusinessObjectListBase<?>) item.getValue();
					for (IBusinessObjectBase boItem : bos) {
						contracts.addAll(this.analyzeContracts(boItem));
					}
				}
			}
		}
		// 分析数据有哪些契约
		if (bo instanceof IBusinessLogicContract) {
			IBusinessLogicsManager logicsManager = BusinessLogicsFactory.createManager();
			Class<?> tmpClass = bo.getClass();
			// 开始检查契约
			while (tmpClass != null) {
				for (Class<?> item : tmpClass.getInterfaces()) {
					boolean exists = false;
					for (Class<?> subItem : item.getInterfaces()) {
						if (subItem.equals(IBusinessLogicContract.class)) {
							// 业务逻辑契约的扩展类型
							exists = true;
							break;
						}
					}
					if (exists) {
						// 存在契约，创建契约对应的逻辑实例
						RuntimeLog.log(RuntimeLog.MSG_LOGICS_EXISTING_CONTRACT, bo.getClass().getName(),
								item.getName());
						IBusinessLogic logic = logicsManager.createLogic(item);
						if (logic == null) {
							throw new NotFoundBusinessLogicsException(item.getName());
						}
						logic.setContract((IBusinessLogicContract) bo);
						logic.setRepository(this.getRepository());
						contracts.add(logic);
					}
				}
				// 检查基类的契约
				tmpClass = tmpClass.getSuperclass();
			}
		}
		return contracts.toArray(new IBusinessLogic[] {});
	}

	private HashMap<IBusinessObjectBase, IBusinessLogic[]> logics;

	/**
	 * 业务逻辑列表
	 * 
	 * @return
	 */
	private HashMap<IBusinessObjectBase, IBusinessLogic[]> getLogics() {
		if (this.logics == null) {
			this.logics = new HashMap<IBusinessObjectBase, IBusinessLogic[]>();
		}
		return this.logics;
	}

	/**
	 * 获取业务逻辑
	 * 
	 * @param bo
	 *            对象
	 * @return
	 */
	protected IBusinessLogic[] getLogics(IBusinessObjectBase bo) {
		if (this.getLogics().containsKey(bo)) {
			// 已存在bo的业务逻辑
			return this.getLogics().get(bo);
		}
		// 不存在bo的业务逻辑
		IBusinessLogic[] logics = this.analyzeContracts(bo);
		this.getLogics().put(bo, logics);
		return logics;
	}

	@Override
	public void forwardLogics(IBusinessObjectBase bo) {
		if (bo == null) {
			return;
		}
		IBusinessLogic[] logics = this.getLogics(bo);
		for (IBusinessLogic logic : logics) {
			logic.forward();
		}
	}

	@Override
	public void reverseLogics(IBusinessObjectBase bo) {
		if (bo == null) {
			return;
		}
		IBusinessLogic[] logics = this.getLogics(bo);
		for (IBusinessLogic logic : logics) {
			logic.reverse();
		}
	}
}
