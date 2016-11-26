package org.colorcoding.ibas.bobas.core;

/**
 * 保存动作监听者
 * 
 * @author Niuren.Zhu
 *
 */
public interface SaveActionsListener {
	/**
	 * 动作通知
	 * 
	 * @param event
	 *            事件参数
	 * @return
	 */
	boolean actionsEvent(SaveActionsEvent event);
}
