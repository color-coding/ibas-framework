package org.colorcoding.ibas.bobas.core;

import java.util.EventObject;

/**
 * 保存动作事件
 * 
 * @author Niuren.Zhu
 *
 */
public class SaveActionEvent extends EventObject {

	private static final long serialVersionUID = 985060795180206279L;

	public SaveActionEvent(Object source) {
		super(source);

	}

	public SaveActionEvent(Object source, SaveActionType type, IBusinessObjectBase bo) {
		this(source);
		this.setType(type);
		this.setTrigger(bo);
	}

	private SaveActionType type;

	/**
	 * 获取-事件类型
	 * 
	 * @return
	 */
	public final SaveActionType getType() {
		return type;
	}

	/**
	 * 设置-事件类型
	 * 
	 * @return
	 */
	private final void setType(SaveActionType type) {
		this.type = type;
	}

	private IBusinessObjectBase trigger;

	/**
	 * 获取-发生事件对象
	 * 
	 * @return
	 */
	public final IBusinessObjectBase getTrigger() {
		return trigger;
	}

	/**
	 * 设置-发生事件对象
	 * 
	 * @return
	 */
	private final void setTrigger(IBusinessObjectBase bo) {
		this.trigger = bo;
	}

}
