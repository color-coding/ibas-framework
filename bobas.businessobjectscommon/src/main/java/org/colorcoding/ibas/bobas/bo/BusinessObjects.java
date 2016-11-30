package org.colorcoding.ibas.bobas.bo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.core.BusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.IBindableBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;

/**
 * 业务对象集合
 * 
 * @author Niuren.Zhu
 *
 * @param <E>
 *            元素类型
 * @param <P>
 *            父项类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObjects", namespace = MyConsts.NAMESPACE_BOBAS_BO)
public abstract class BusinessObjects<E extends IBusinessObject, P extends IBusinessObject>
        extends BusinessObjectListBase<E> implements IBusinessObjects<E, P> {

    /**
     * 
     */
    private static final long serialVersionUID = 7360645136974073845L;

    public BusinessObjects() {
        this.setChangeElementStatus(true);
        this.setChangeParentStatus(true);
        // 监听自身改变事件
        this.addPropertyChangeListener(this.propertyListener);
    }

    public BusinessObjects(P parent) {
        this();
        this.setParent(parent);
    }

    private boolean changeElementStatus;

    /**
     * 是否自动改变子项状态，父项变化
     * 
     * @return
     */
    protected final boolean isChangeElementStatus() {
        return changeElementStatus;
    }

    protected final void setChangeElementStatus(boolean value) {
        this.changeElementStatus = value;
    }

    private boolean changeParentStatus;

    /**
     * 是否自动改变父项状态，子项变化
     * 
     * @return
     */
    protected final boolean isChangeParentStatus() {
        return changeParentStatus;
    }

    protected final void setChangeParentStatus(boolean value) {
        this.changeParentStatus = value;
    }

    private P parent = null;

    protected final P getParent() {
        return parent;
    }

    protected final void setParent(P parent) {
        if (this.parent instanceof IBindableBase) {
            // 移出监听属性改变
            IBindableBase boItem = (IBindableBase) this.parent;
            boItem.removePropertyChangeListener(this.propertyListener);
        }
        this.parent = parent;
        if (this.parent instanceof IBindableBase) {
            // 监听属性改变
            IBindableBase boItem = (IBindableBase) this.parent;
            boItem.addPropertyChangeListener(this.propertyListener);
        }
    }

    /**
     * 指向当前实例
     */
    private BusinessObjects<E, P> that = this;
    /**
     * 属性监听实例（隐藏接口实现）
     */
    private PropertyChangeListener propertyListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt != null && evt.getPropertyName() != null) {
                if (evt.getSource() == that.getParent()) {
                    // 父项属性改变
                    that.onParentPropertyChanged(evt);
                } else if (that.contains(evt.getSource())) {
                    // 此集合中的子项的属性改变
                    if (evt.getPropertyName() != null && evt.getPropertyName().equals("isDirty")
                            && evt.getNewValue().equals(true)) {
                        // 元素状态为Dirty时修改父项状态
                        if (that.getParent() instanceof ITrackStatusOperator) {
                            // 改变父项的状态跟踪
                            ITrackStatusOperator statusOperator = (ITrackStatusOperator) that.getParent();
                            statusOperator.markDirty();
                        }
                    } else {
                        that.onElementPropertyChanged(evt);
                    }
                } else if (evt.getSource() == that && evt.getPropertyName().equals(PROPERTY_NAME_SIZE)) {
                    if (that.parent != null && !that.parent.isLoading()) {
                        // 集合自身的属性改变事件
                        if (that.getParent() instanceof ITrackStatusOperator) {
                            // 改变父项的状态跟踪
                            ITrackStatusOperator statusOperator = (ITrackStatusOperator) that.getParent();
                            statusOperator.markDirty();
                        }
                    }
                }
            }
        }

    };

    @Override
    public final boolean add(E item) {
        return super.add(item);
    }

    /**
     * 获取LineId编号
     * 
     * @return
     */
    private int getNextLineId() {
        int lineId = 0;
        for (E item : this) {
            if (item instanceof IBOLine) {
                IBOLine line = (IBOLine) item;
                if (line.getLineId() > lineId) {
                    lineId = line.getLineId();
                }
            }
        }
        lineId = lineId + 1;
        return lineId;
    }

    @Override
    protected void afterAddItem(E item) {
        // 调用基类方法
        super.afterAddItem(item);
        // 额外逻辑
        if (item instanceof IBindableBase) {
            // 监听属性改变
            IBindableBase boItem = (IBindableBase) item;
            boItem.addPropertyChangeListener(this.propertyListener);
        }
        // 修正子项编号
        if (item instanceof IBOLine) {
            IBOLine line = (IBOLine) item;
            if (line.getLineId() <= 0) {
                line.setLineId(this.getNextLineId());
            }
        }
        // 没父项，退出
        if (this.getParent() == null) {
            return;
        }
        // 添加子项即给子项主键赋值
        if (item instanceof IBODocumentLine) {
            IBODocumentLine child = (IBODocumentLine) item;
            if (this.getParent() instanceof IBODocument) {
                IBODocument parent = (IBODocument) this.getParent();
                child.setDocEntry(parent.getDocEntry());
            } else if (this.getParent() instanceof IBODocumentLine) {
                IBODocumentLine parent = (IBODocumentLine) this.getParent();
                child.setDocEntry(parent.getDocEntry());
            }
        } else if (item instanceof IBOMasterDataLine) {
            IBOMasterDataLine child = (IBOMasterDataLine) item;
            if (this.getParent() instanceof IBOMasterData) {
                IBOMasterData parent = (IBOMasterData) this.getParent();
                child.setCode(parent.getCode());
            } else if (this.getParent() instanceof IBOMasterDataLine) {
                IBOMasterDataLine parent = (IBOMasterDataLine) this.getParent();
                child.setCode(parent.getCode());
            }
        } else if (item instanceof IBOSimpleLine) {
            IBOSimpleLine child = (IBOSimpleLine) item;
            if (this.getParent() instanceof IBOSimple) {
                IBOSimple parent = (IBOSimple) this.getParent();
                child.setObjectKey(parent.getObjectKey());
            } else if (this.getParent() instanceof IBOSimpleLine) {
                IBOSimpleLine parent = (IBOSimpleLine) this.getParent();
                child.setObjectKey(parent.getObjectKey());
            }
        }
        // 父项读取数据中，退出
        if (this.getParent().isLoading()) {
            return;
        }
        // 处理单据状态
        if (item instanceof IBODocumentLine) {
            IBODocumentLine child = (IBODocumentLine) item;
            if (this.getParent() instanceof IBODocument) {
                IBODocument parent = (IBODocument) this.getParent();
                child.setLineStatus(parent.getDocumentStatus());
            } else if (this.getParent() instanceof IBODocumentLine) {
                IBODocumentLine parent = (IBODocumentLine) this.getParent();
                child.setLineStatus(parent.getLineStatus());
            }
        }
    }

    /**
     * 当父项属性发生变化
     * 
     * @param evt
     */
    protected void onParentPropertyChanged(PropertyChangeEvent evt) {
        // 父项空，退出
        if (this.getParent() == null) {
            return;
        }
        if (IBOSimple.MASTER_PRIMARY_KEY_NAME.equals(evt.getPropertyName())) {
            // 简单对象类
            if (evt.getSource() instanceof IBOSimple) {
                // 头
                IBOSimple parentItem = (IBOSimple) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBOSimpleLine) {
                        IBOSimpleLine lineItem = (IBOSimpleLine) item;
                        lineItem.setObjectKey(parentItem.getObjectKey());
                    } else if (item instanceof IBOSimple) {
                        IBOSimple lineItem = (IBOSimple) item;
                        lineItem.setObjectKey(parentItem.getObjectKey());
                    }
                }
            } else if (evt.getSource() instanceof IBOSimpleLine) {
                // 行
                IBOSimpleLine parentItem = (IBOSimpleLine) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBOSimpleLine) {
                        IBOSimpleLine lineItem = (IBOSimpleLine) item;
                        lineItem.setObjectKey(parentItem.getObjectKey());
                    } else if (item instanceof IBOSimple) {
                        IBOSimple lineItem = (IBOSimple) item;
                        lineItem.setObjectKey(parentItem.getObjectKey());
                    }
                }
            }

        } else if (IBOMasterData.MASTER_PRIMARY_KEY_NAME.equals(evt.getPropertyName())) {
            // 主数据类
            if (evt.getSource() instanceof IBOMasterData) {
                // 头
                IBOMasterData parentItem = (IBOMasterData) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBOMasterDataLine) {
                        IBOMasterDataLine lineItem = (IBOMasterDataLine) item;
                        lineItem.setCode(parentItem.getCode());
                    } else if (item instanceof IBOMasterData) {
                        IBOMasterData lineItem = (IBOMasterData) item;
                        lineItem.setCode(parentItem.getCode());
                    }
                }
            } else if (evt.getSource() instanceof IBOMasterDataLine) {
                // 行
                IBOMasterDataLine parentItem = (IBOMasterDataLine) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBOMasterDataLine) {
                        IBOMasterDataLine lineItem = (IBOMasterDataLine) item;
                        lineItem.setCode(parentItem.getCode());
                    } else if (item instanceof IBOMasterData) {
                        IBOMasterData lineItem = (IBOMasterData) item;
                        lineItem.setCode(parentItem.getCode());
                    }
                }
            }
        } else if (IBODocument.MASTER_PRIMARY_KEY_NAME.equals(evt.getPropertyName())) {
            // 单据类
            if (evt.getSource() instanceof IBODocument) {
                // 头
                IBODocument parentItem = (IBODocument) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBODocumentLine) {
                        IBODocumentLine lineItem = (IBODocumentLine) item;
                        lineItem.setDocEntry(parentItem.getDocEntry());
                    } else if (item instanceof IBODocument) {
                        IBODocument lineItem = (IBODocument) item;
                        lineItem.setDocEntry(parentItem.getDocEntry());
                    }
                }
            } else if (evt.getSource() instanceof IBODocumentLine) {
                // 行
                IBODocumentLine parentItem = (IBODocumentLine) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBODocumentLine) {
                        IBODocumentLine lineItem = (IBODocumentLine) item;
                        lineItem.setDocEntry(parentItem.getDocEntry());
                    } else if (item instanceof IBODocument) {
                        IBODocument lineItem = (IBODocument) item;
                        lineItem.setDocEntry(parentItem.getDocEntry());
                    }
                }
            }
        }
        // 加载中，退出
        if (this.getParent().isLoading()) {
            return;
        }
        // 状态变化
        if (this.isChangeElementStatus()) {
            this.changeElementStatus(evt);
        }
    }

    /**
     * 当元素属性发生变化
     * 
     * @param evt
     */
    protected void onElementPropertyChanged(PropertyChangeEvent evt) {
        // 加载中，退出
        if (this.getParent() == null || this.getParent().isLoading()) {
            return;
        }
        if (this.isChangeParentStatus()) {
            this.changeParentStatus(evt);
        }
    }

    /**
     * 父项数据变化时触发改变元素数据
     * 
     * @param evt
     */
    private void changeElementStatus(PropertyChangeEvent evt) {
        // 父项的属性改变
        // 可取消对象
        if (evt.getPropertyName().equals("Canceled")) {
            if (evt.getSource() instanceof IBOTagCanceled) {
                // 头
                IBOTagCanceled parentTag = (IBOTagCanceled) evt.getSource();
                for (E item : this) {
                    if (!(item instanceof IBOTagCanceled)) {
                        continue;
                    }
                    IBOTagCanceled lineTag = (IBOTagCanceled) item;
                    lineTag.setCanceled(parentTag.getCanceled());
                }
            }
        }
        // 标记删除对象
        else if (evt.getPropertyName().equals("Deleted")) {
            if (evt.getSource() instanceof IBOTagDeleted) {
                // 头
                IBOTagDeleted parentTag = (IBOTagDeleted) evt.getSource();
                for (E item : this) {
                    if (!(item instanceof IBOTagDeleted)) {
                        continue;
                    }
                    IBOTagDeleted lineTag = (IBOTagDeleted) item;
                    lineTag.setDeleted(parentTag.getDeleted());
                }
            }
        } else if (evt.getPropertyName().equals("LineStatus") || evt.getPropertyName().equals("DocumentStatus")
                || evt.getPropertyName().equals("Status")) {
            // 单据类
            if (evt.getSource() instanceof IBODocument) {
                // 头
                IBODocument parentItem = (IBODocument) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBODocumentLine) {
                        IBODocumentLine lineItem = (IBODocumentLine) item;
                        if (evt.getPropertyName().equals("DocumentStatus")) {
                            lineItem.setLineStatus(parentItem.getDocumentStatus());
                        } else if (evt.getPropertyName().equals("Status")) {
                            lineItem.setStatus(parentItem.getStatus());
                        }
                    } else if (item instanceof IBODocument) {
                        IBODocument lineItem = (IBODocument) item;
                        if (evt.getPropertyName().equals("DocumentStatus")) {
                            lineItem.setDocumentStatus(parentItem.getDocumentStatus());
                        } else if (evt.getPropertyName().equals("Status")) {
                            lineItem.setStatus(parentItem.getStatus());
                        }
                    }
                }
            } else if (evt.getSource() instanceof IBODocumentLine) {
                // 行
                IBODocumentLine parentItem = (IBODocumentLine) evt.getSource();
                for (E item : this) {
                    if (item instanceof IBODocumentLine) {
                        IBODocumentLine lineItem = (IBODocumentLine) item;
                        if (evt.getPropertyName().equals("DocumentStatus")) {
                            lineItem.setLineStatus(parentItem.getLineStatus());
                        } else if (evt.getPropertyName().equals("Status")) {
                            lineItem.setStatus(parentItem.getStatus());
                        }
                    } else if (item instanceof IBODocument) {
                        IBODocument lineItem = (IBODocument) item;
                        if (evt.getPropertyName().equals("DocumentStatus")) {
                            lineItem.setDocumentStatus(parentItem.getLineStatus());
                        } else if (evt.getPropertyName().equals("Status")) {
                            lineItem.setStatus(parentItem.getStatus());
                        }
                    }
                }
            }
        }
    }

    /**
     * 元素数据变化时触发改变父项数据
     * 
     * @param evt
     */
    private void changeParentStatus(PropertyChangeEvent evt) {
        // 不是关注的属性改变退出
        IFieldData parentField = null;
        // 被引用，子项被引用，父项被引用
        if (evt.getPropertyName().equals("Referenced")) {
            if (evt.getSource() instanceof IBOReferenced) {
                if (this.getParent() instanceof IBOReferenced && this.getParent() instanceof IManageFields) {
                    parentField = ((IManageFields) this.getParent()).getField(evt.getPropertyName());
                    if (parentField == null) {
                        return;
                    }
                    // 子项全部为修改值，则父项也修改
                    for (E item : this) {
                        if (!(item instanceof IBOReferenced)) {
                            continue;
                        }
                        IBOReferenced lineItem = (IBOReferenced) item;
                        if (lineItem.getReferenced() == emYesNo.Yes) {
                            // 任意子项被引用，父项被引用
                            parentField.setValue(emYesNo.Yes);
                            return;
                        }
                    }
                }
            }
        }
        // 可取消
        else if (evt.getPropertyName().equals("Canceled")) {
            if (evt.getSource() instanceof IBOTagCanceled) {
                if (this.getParent() instanceof IBOTagCanceled && this.getParent() instanceof IManageFields) {
                    parentField = ((IManageFields) this.getParent()).getField(evt.getPropertyName());
                    if (parentField == null) {
                        return;
                    }
                    IBOTagCanceled lineItem = (IBOTagCanceled) evt.getSource();
                    emYesNo boCanceled = lineItem.getCanceled();
                    // 子项全部为修改值，则父项也修改
                    for (E item : this) {
                        if (!(item instanceof IBOTagCanceled)) {
                            continue;
                        }
                        lineItem = (IBOTagCanceled) item;
                        if (lineItem.getCanceled() != boCanceled) {
                            // 子项有不同值，退出，优先不取消
                            parentField.setValue(emYesNo.No);
                            return;
                        }
                    }
                    parentField.setValue(boCanceled);
                }
            }
        }
        // 可删除
        else if (evt.getPropertyName().equals("Deleted")) {
            if (evt.getSource() instanceof IBOTagDeleted) {
                if (this.getParent() instanceof IBOTagDeleted && this.getParent() instanceof IManageFields) {
                    parentField = ((IManageFields) this.getParent()).getField(evt.getPropertyName());
                    if (parentField == null) {
                        return;
                    }
                    IBOTagDeleted lineItem = (IBOTagDeleted) evt.getSource();
                    emYesNo boDeleted = lineItem.getDeleted();
                    // 子项全部为修改值，则父项也修改
                    for (E item : this) {
                        if (!(item instanceof IBOTagDeleted)) {
                            continue;
                        }
                        lineItem = (IBOTagDeleted) item;
                        if (lineItem.getDeleted() != boDeleted) {
                            // 子项有不同值，退出，优先不删除
                            parentField.setValue(emYesNo.No);
                            return;
                        }
                    }
                    parentField.setValue(boDeleted);
                }
            }
        }
        // 单据对象
        else if (evt.getPropertyName().equals("LineStatus") || evt.getPropertyName().equals("Status")) {
            if (evt.getSource() instanceof IBODocumentLine && this.getParent() instanceof IManageFields) {
                IBODocumentLine lineItem = (IBODocumentLine) evt.getSource();
                if (this.getParent() instanceof IBODocument) {
                    // 父项是单据
                    IBODocument parent = (IBODocument) this.getParent();
                    if (evt.getPropertyName().equals("LineStatus")) {
                        // 使用字段赋值避免触发事件
                        parentField = ((IManageFields) parent).getField("DocumentStatus");
                        if (parentField == null) {
                            return;
                        }
                        emDocumentStatus boLineStatus = lineItem.getLineStatus();
                        // 子项全部为修改值，则父项也修改
                        for (E item : this) {
                            if (!(item instanceof IBODocumentLine)) {
                                continue;
                            }
                            lineItem = (IBODocumentLine) item;
                            if (lineItem.getLineStatus() != boLineStatus) {
                                // 子项有不同值
                                if (parent.getDocumentStatus() == emDocumentStatus.Planned) {
                                    // 父项计划状态
                                    if (boLineStatus.ordinal() > emDocumentStatus.Planned.ordinal()) {
                                        // 子项变为计划以上状态
                                        parentField.setValue(emDocumentStatus.Released);
                                    }
                                } else {
                                    if (parent.getDocumentStatus().ordinal() > boLineStatus.ordinal()) {
                                        // 父项高于修改状态，父项降低
                                        if (boLineStatus == emDocumentStatus.Planned)
                                            // 最低到Relase
                                            parentField.setValue(emDocumentStatus.Released);
                                        else
                                            parentField.setValue(boLineStatus);
                                    }
                                    // 父项低于修改状态，等待全部修改
                                }
                                // 退出
                                return;
                            }
                        }
                        parentField.setValue(boLineStatus);
                    } else if (evt.getPropertyName().equals("Status")) {
                        // 使用字段赋值避免触发事件
                        parentField = ((IManageFields) parent).getField(evt.getPropertyName());
                        if (parentField == null) {
                            return;
                        }
                        emBOStatus boStatus = lineItem.getStatus();
                        // 子项全部为修改值，则父项也修改
                        for (E item : this) {
                            if (!(item instanceof IBODocumentLine)) {
                                continue;
                            }
                            lineItem = (IBODocumentLine) item;
                            if (lineItem.getStatus() != boStatus) {
                                // 子项有不同值，退出，优先不关闭
                                parentField.setValue(emBOStatus.Open);
                                return;
                            }
                        }
                        parentField.setValue(boStatus);
                    }
                } else if (this.getParent() instanceof IBODocumentLine) {
                    // 父项是单据行
                    IBODocumentLine parent = (IBODocumentLine) this.getParent();
                    if (evt.getPropertyName().equals("LineStatus")) {
                        // 使用字段赋值避免触发事件
                        parentField = ((IManageFields) parent).getField(evt.getPropertyName());
                        if (parentField == null) {
                            return;
                        }
                        emDocumentStatus boLineStatus = lineItem.getLineStatus();
                        // 子项全部为修改值，则父项也修改
                        for (E item : this) {
                            if (!(item instanceof IBODocumentLine)) {
                                continue;
                            }
                            lineItem = (IBODocumentLine) item;
                            if (lineItem.getLineStatus() != boLineStatus) {
                                if (parent.getLineStatus() == emDocumentStatus.Planned) {
                                    // 父项计划状态
                                    if (boLineStatus.ordinal() > emDocumentStatus.Planned.ordinal()) {
                                        // 子项变为计划以上状态
                                        parentField.setValue(emDocumentStatus.Released);
                                    }
                                } else {
                                    if (parent.getLineStatus().ordinal() > boLineStatus.ordinal()) {
                                        // 父项高于修改状态，父项降低
                                        if (boLineStatus == emDocumentStatus.Planned)
                                            // 最低到Relase
                                            parentField.setValue(emDocumentStatus.Released);
                                        else
                                            parentField.setValue(boLineStatus);
                                    }
                                    // 父项低于修改状态，等待全部修改
                                }
                                // 退出
                                return;
                            }
                        }
                        parentField.setValue(boLineStatus);
                    } else if (evt.getPropertyName().equals("Status")) {
                        // 使用字段赋值避免触发事件
                        parentField = ((IManageFields) parent).getField(evt.getPropertyName());
                        if (parentField == null) {
                            return;
                        }
                        emBOStatus boStatus = lineItem.getStatus();
                        // 子项全部为修改值，则父项也修改
                        for (E item : this) {
                            if (!(item instanceof IBODocumentLine)) {
                                continue;
                            }
                            lineItem = (IBODocumentLine) item;
                            if (lineItem.getStatus() != boStatus) {
                                // 子项有不同值，退出，优先不关闭
                                parentField.setValue(emBOStatus.Open);
                                return;
                            }
                        }
                        parentField.setValue(boStatus);
                    }
                }
            }
        }
    }

    @Override
    protected void afterRemoveItem(E item) {
        // 调用基类方法
        super.afterRemoveItem(item);
        // 额外逻辑
        if (item instanceof IBindableBase) {
            // 移出监听属性改变
            IBindableBase boItem = (IBindableBase) item;
            boItem.removePropertyChangeListener(this.propertyListener);
        }
    }

    @Override
    public ICriteria getElementCriteria() {
        if (this.getParent() == null) {
            return null;
        }
        ICriteria criteria = this.getParent().getCriteria();
        if (IBOLine.class.isAssignableFrom(this.getElementType())) {
            // 元素类型是行类型，则添加排序字段
            ISort sort = criteria.getSorts().create();
            sort.setAlias(IBOLine.SECONDARY_PRIMARY_KEY_NAME);
            sort.setSortType(SortType.st_Ascending);
        }
        return criteria;
    }

    @Override
    public final void delete(E item) {
        if (this.contains(item)) {
            // 集合中的元素
            if (item.isNew()) {
                this.remove(item);
            } else {
                item.delete();
            }
        }
    }

    @Override
    public final void delete(int index) {
        E item = this.get(index);
        this.delete(item);
    }

    @Override
    public final void deleteAll() {
        for (int i = this.size() - 1; i >= 0; i--) {
            this.delete(i);
        }
    }
}
