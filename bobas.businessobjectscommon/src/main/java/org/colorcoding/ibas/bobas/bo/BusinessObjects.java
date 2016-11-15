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
		extends BusinessObjectListBase<E> implements IBusinessObjects<E, P>, PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7360645136974073845L;

	public BusinessObjects() {
		this.setChangeElementStatus(true);
		this.setChangeParentStatus(true);
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
	protected boolean isChangeElementStatus() {
		return changeElementStatus;
	}

	protected void setChangeElementStatus(boolean value) {
		this.changeElementStatus = value;
	}

	private boolean changeParentStatus;

	/**
	 * 是否自动改变父项状态，子项变化
	 * 
	 * @return
	 */
	protected boolean isChangeParentStatus() {
		return changeParentStatus;
	}

	protected void setChangeParentStatus(boolean value) {
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
			boItem.removePropertyChangeListener(this);
		}
		this.parent = parent;
		if (this.parent instanceof IBindableBase) {
			// 监听属性改变
			IBindableBase boItem = (IBindableBase) this.parent;
			boItem.addPropertyChangeListener(this);
		}
	}

	@Override
	public boolean add(E item) {
		return super.add(item);
	}

	/**
	 * 获取LineId编号
	 * 
	 * @return
	 */
	protected int getMaxLineId() {
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
		if (item instanceof IBODocumentLine) {
			IBODocumentLine docItem = (IBODocumentLine) item;
			if (this.getParent() instanceof IBODocument) {
				IBODocument doc = (IBODocument) this.getParent();
				docItem.setDocEntry(doc.getDocEntry());
				docItem.setCanceled(doc.getCanceled());
				docItem.setLineStatus(doc.getDocumentStatus());
				docItem.setStatus(doc.getStatus());
			}
			if (this.getParent() instanceof IBODocumentLine) {
				IBODocumentLine doc = (IBODocumentLine) this.getParent();
				docItem.setDocEntry(doc.getDocEntry());
				docItem.setCanceled(doc.getCanceled());
				docItem.setLineStatus(doc.getLineStatus());
				docItem.setStatus(doc.getStatus());
			}
		} else if (item instanceof IBOMasterDataLine) {
			IBOMasterDataLine masItem = (IBOMasterDataLine) item;
			if (this.getParent() instanceof IBOMasterData) {
				IBOMasterData mas = (IBOMasterData) this.getParent();
				masItem.setCode(mas.getCode());
			}
		} else if (item instanceof IBOSimpleLine) {
			IBOSimpleLine simItem = (IBOSimpleLine) item;
			if (this.getParent() instanceof IBOSimple) {
				IBOSimple sim = (IBOSimple) this.getParent();
				simItem.setObjectKey(sim.getObjectKey());
			}
		}
		if (item instanceof IBOLine) {
			IBOLine line = (IBOLine) item;
			if (line.getLineId() <= 0) {
				line.setLineId(this.getMaxLineId());
			}
		}
		if (item instanceof IBindableBase) {
			// 监听属性改变
			IBindableBase boItem = (IBindableBase) item;
			boItem.addPropertyChangeListener(this);
		}
		// 集合元素发生变化
		if (this.parent instanceof ITrackStatusOperator) {
			// 改变父项的状态跟踪
			ITrackStatusOperator statusOperator = (ITrackStatusOperator) this.parent;
			statusOperator.markDirty();
		}
	}

	/**
	 * 监听父项及子项的属性改变
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt != null && evt.getPropertyName() != null) {
			if (evt.getSource() == this.parent && this.isChangeElementStatus()) {
				// 父项的属性改变
				if (evt.getPropertyName().equals("DocEntry") || evt.getPropertyName().equals("LineStatus")
						|| evt.getPropertyName().equals("Canceled") || evt.getPropertyName().equals("DocumentStatus")
						|| evt.getPropertyName().equals("Status")) {
					// 单据类
					if (evt.getSource() instanceof IBODocument) {
						// 头
						IBODocument parentItem = (IBODocument) evt.getSource();
						for (E item : this) {
							if (!(item instanceof IBODocumentLine)) {
								continue;
							}
							IBODocumentLine lineItem = (IBODocumentLine) item;
							if (evt.getPropertyName().equals("DocEntry")) {
								lineItem.setDocEntry(parentItem.getDocEntry());
							} else if (evt.getPropertyName().equals("DocumentStatus")) {
								lineItem.setLineStatus(parentItem.getDocumentStatus());
							} else if (evt.getPropertyName().equals("Canceled")) {
								lineItem.setCanceled(parentItem.getCanceled());
							} else if (evt.getPropertyName().equals("Status")) {
								lineItem.setStatus(parentItem.getStatus());
							}
						}
					}
					if (evt.getSource() instanceof IBODocumentLine) {
						// 行
						IBODocumentLine parentItem = (IBODocumentLine) evt.getSource();
						for (E item : this) {
							if (!(item instanceof IBODocumentLine)) {
								continue;
							}
							IBODocumentLine lineItem = (IBODocumentLine) item;
							if (evt.getPropertyName().equals("DocEntry")) {
								lineItem.setDocEntry(parentItem.getDocEntry());
							} else if (evt.getPropertyName().equals("LineStatus")) {
								lineItem.setLineStatus(parentItem.getLineStatus());
							} else if (evt.getPropertyName().equals("Canceled")) {
								lineItem.setCanceled(parentItem.getCanceled());
							} else if (evt.getPropertyName().equals("Status")) {
								lineItem.setStatus(parentItem.getStatus());
							}
						}
					}
				}
				if (evt.getPropertyName().equals("ObjectKey")) {
					// 简单对象类
					if (evt.getSource() instanceof IBOSimple) {
						// 头
						IBOSimple parentItem = (IBOSimple) evt.getSource();
						for (E item : this) {
							if (!(item instanceof IBOSimpleLine)) {
								continue;
							}
							IBOSimpleLine lineItem = (IBOSimpleLine) item;
							if (evt.getPropertyName().equals("ObjectKey")) {
								lineItem.setObjectKey(parentItem.getObjectKey());
							}
						}
					}
					if (evt.getSource() instanceof IBOSimpleLine) {
						// 行
						IBOSimpleLine parentItem = (IBOSimpleLine) evt.getSource();
						for (E item : this) {
							if (!(item instanceof IBOSimpleLine)) {
								continue;
							}
							IBOSimpleLine lineItem = (IBOSimpleLine) item;
							if (evt.getPropertyName().equals("ObjectKey")) {
								lineItem.setObjectKey(parentItem.getObjectKey());
							}
						}
					}

				}
				if (evt.getPropertyName().equals("Code")) {
					// 主数据类
					if (evt.getSource() instanceof IBOMasterData) {
						// 头
						IBOMasterData parentItem = (IBOMasterData) evt.getSource();
						for (E item : this) {
							if (!(item instanceof IBOMasterDataLine)) {
								continue;
							}
							IBOMasterDataLine lineItem = (IBOMasterDataLine) item;
							if (evt.getPropertyName().equals("Code")) {
								lineItem.setCode(parentItem.getCode());
							}
						}
					}
					if (evt.getSource() instanceof IBOMasterDataLine) {
						// 行
						IBOMasterDataLine parentItem = (IBOMasterDataLine) evt.getSource();
						for (E item : this) {
							if (!(item instanceof IBOMasterDataLine)) {
								continue;
							}
							IBOMasterDataLine lineItem = (IBOMasterDataLine) item;
							if (evt.getPropertyName().equals("Code")) {
								lineItem.setCode(parentItem.getCode());
							}
						}
					}
				}
			} else if (this.contains(evt.getSource())) {
				// 此集合中的子项的属性改变
				if (evt.getPropertyName() != null && evt.getPropertyName().equals("isDirty")
						&& evt.getNewValue().equals(true)) {
					// 元素状态为Dirty时修改父项状态
					if (this.parent instanceof ITrackStatusOperator) {
						// 改变父项的状态跟踪
						ITrackStatusOperator statusOperator = (ITrackStatusOperator) this.parent;
						statusOperator.markDirty();
					}
				} else {
					if (this.isChangeParentStatus() && (evt.getPropertyName().equals("LineStatus")
							|| evt.getPropertyName().equals("Canceled") || evt.getPropertyName().equals("Status"))) {
						IFieldData parentField = null;
						if (evt.getSource() instanceof IBODocumentLine) {
							IBODocumentLine lineItem = (IBODocumentLine) evt.getSource();
							if (this.getParent() instanceof IBODocument && this.getParent() instanceof IManageFields) {
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
								} else if (evt.getPropertyName().equals("Canceled")) {
									// 使用字段赋值避免触发事件
									parentField = ((IManageFields) parent).getField(evt.getPropertyName());
									if (parentField == null) {
										return;
									}
									emYesNo boCanceled = lineItem.getCanceled();
									// 子项全部为修改值，则父项也修改
									for (E item : this) {
										if (!(item instanceof IBODocumentLine)) {
											continue;
										}
										lineItem = (IBODocumentLine) item;
										if (lineItem.getCanceled() != boCanceled) {
											// 子项有不同值，退出，优先不取消
											parent.setCanceled(emYesNo.No);
											return;
										}
									}
									parentField.setValue(boCanceled);
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
											parent.setStatus(emBOStatus.Open);
											return;
										}
									}
									parentField.setValue(boStatus);
								}
							}
							if (this.getParent() instanceof IBODocumentLine) {
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
								} else if (evt.getPropertyName().equals("Canceled")) {
									// 使用字段赋值避免触发事件
									parentField = ((IManageFields) parent).getField(evt.getPropertyName());
									if (parentField == null) {
										return;
									}
									emYesNo boCanceled = lineItem.getCanceled();
									// 子项全部为修改值，则父项也修改
									for (E item : this) {
										if (!(item instanceof IBODocumentLine)) {
											continue;
										}
										lineItem = (IBODocumentLine) item;
										if (lineItem.getCanceled() != boCanceled) {
											// 子项有不同值，退出，优先不取消
											parent.setCanceled(emYesNo.No);
											return;
										}
									}
									parentField.setValue(boCanceled);
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
											parent.setStatus(emBOStatus.Open);
											return;
										}
									}
									parentField.setValue(boStatus);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void afterRemoveItem(E item) {
		if (item instanceof IBindableBase) {
			// 移出监听属性改变
			IBindableBase boItem = (IBindableBase) item;
			boItem.removePropertyChangeListener(this);
		}
		// 集合元素发生变化
		if (this.parent instanceof ITrackStatusOperator) {
			// 改变父项的状态跟踪
			ITrackStatusOperator statusOperator = (ITrackStatusOperator) this.parent;
			statusOperator.markDirty();
		}
	}

	@Override
	public ICriteria getElementCriteria() {
		if (this.parent == null) {
			return null;
		}
		ICriteria criteria = this.parent.getCriteria();
		if (IBOLine.class.isAssignableFrom(this.getElementType())) {
			// 元素类型是行类型，则添加排序字段
			ISort sort = criteria.getSorts().create();
			sort.setAlias("LineId");
			sort.setSortType(SortType.st_Ascending);
		}
		return criteria;
	}

	@Override
	public void delete(E item) {
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
	public void delete(int index) {
		E item = this.get(index);
		this.delete(item);
	}

	@Override
	public void deleteAll() {
		for (int i = this.size() - 1; i >= 0; i--) {
			this.delete(i);
		}
	}
}
