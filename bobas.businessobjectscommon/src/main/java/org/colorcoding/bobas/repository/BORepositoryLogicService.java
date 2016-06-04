package org.colorcoding.bobas.repository;

import org.colorcoding.bobas.MyConfiguration;
import org.colorcoding.bobas.core.SaveActionsEvent;

/**
 * 业务仓库服务，带业务逻辑处理
 * 
 * 
 * @author niuren.zhu
 *
 */
public class BORepositoryLogicService extends BORepositoryService {

	@Override
	public boolean actionsNotification(SaveActionsEvent event) {
		if (!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_LOGICS, false)) {
			// 执行业务逻辑
			// this.runLogics(event.getType(), event.getBO());
		}
		// 运行基类方法
		return super.actionsNotification(event);
	}


}
