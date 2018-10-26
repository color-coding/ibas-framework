package org.colorcoding.ibas.bobas.core;

import java.util.EventListener;

/**
 * 仓库保存监听者
 * 
 * @author Niuren.Zhu
 *
 */
public interface RepositorySaveListener extends EventListener {
	/**
	 * 仓库保存通知
	 * 
	 * @param event 事件参数
	 * @throws RepositoryException
	 */
	void repositorySave(RepositorySaveEvent event) throws RepositoryException;
}
