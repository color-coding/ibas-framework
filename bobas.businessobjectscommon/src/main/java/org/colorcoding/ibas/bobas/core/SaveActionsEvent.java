package org.colorcoding.ibas.bobas.core;

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

    public SaveActionsEvent(Object source, SaveActionsType type, IBusinessObjectBase bo, IBusinessObjectBase root) {
        this(source);
        this.setType(type);
        this.setBO(bo);
        this.setRootBO(root);
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
    private final void setType(SaveActionsType type) {
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
    private final void setBO(IBusinessObjectBase bo) {
        this.bo = bo;
    }

    private IBusinessObjectBase rootBO;

    /**
     * 获取-根对象
     * 
     * @return
     */
    public final IBusinessObjectBase getRootBO() {
        return rootBO;
    }

    private final void setRootBO(IBusinessObjectBase rootBO) {
        this.rootBO = rootBO;
    }

}
