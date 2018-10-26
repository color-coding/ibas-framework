package org.colorcoding.ibas.bobas.core;

import java.util.EventObject;

/**
 * 仓库保存事件
 * 
 * @author Niuren.Zhu
 *
 */
public class RepositorySaveEvent extends EventObject {

	private static final long serialVersionUID = 985060795180206279L;

	public RepositorySaveEvent(Object source) {
		super(source);
	}

	public RepositorySaveEvent(Object source, RepositorySaveEventType type, IBusinessObjectBase bo) {
		this(source);
		this.setType(type);
		this.setTrigger(bo);
	}

	private RepositorySaveEventType type;

	/**
	 * 获取-事件类型
	 * 
	 * @return
	 */
	public final RepositorySaveEventType getType() {
		return type;
	}

	/**
	 * 设置-事件类型
	 * 
	 * @return
	 */
	private final void setType(RepositorySaveEventType type) {
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
