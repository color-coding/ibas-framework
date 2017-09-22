package org.colorcoding.ibas.bobas.core;

/**
 * 保存动作监听者
 * 
 * @author Niuren.Zhu
 *
 */
public interface SaveActionListener {
	/**
	 * 动作通知
	 * 
	 * @param event
	 *            事件参数
	 * @throws SaveActionException
	 */
	void onActionEvent(SaveActionEvent event) throws RepositoryException;
}
