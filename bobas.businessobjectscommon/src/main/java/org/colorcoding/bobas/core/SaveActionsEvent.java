package org.colorcoding.bobas.core;

import java.util.EventObject;

/**
 * 保存动作事件
 * 
 * @author Niuren.Zhu
 *
 */
public class SaveActionsEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 985060795180206279L;

	public SaveActionsEvent(Object source) {
		super(source);

	}

	public SaveActionsEvent(Object source, SaveActionsType type, IBusinessObjectBase bo) {
		this(source);
		this.setType(type);
		this.setBO(bo);
	}

	private SaveActionsType type;

	/**
	 * 获取-事件类型
	 * 
	 * @return
	 */
	public final SaveActionsType getType() {
		return type;
	}

	/**
	 * 设置-事件类型
	 * 
	 * @return
	 */
	public final void setType(SaveActionsType type) {
		this.type = type;
	}

	private IBusinessObjectBase bo;

	/**
	 * 获取-发生事件对象
	 * 
	 * @return
	 */
	public final IBusinessObjectBase getBO() {
		return bo;
	}

	/**
	 * 设置-发生事件对象
	 * 
	 * @return
	 */
	public final void setBO(IBusinessObjectBase bo) {
		this.bo = bo;
	}
}
