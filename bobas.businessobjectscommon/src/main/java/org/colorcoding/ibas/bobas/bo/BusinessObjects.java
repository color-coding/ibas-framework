package org.colorcoding.ibas.bobas.bo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.core.IBindable;
import org.colorcoding.ibas.bobas.core.Trackable;
import org.colorcoding.ibas.bobas.data.ArrayList;

/**
 * 业务对象集合
 *
 * @param <E> 元素类型
 * @param <P> 父项类型
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BusinessObjects<E extends IBusinessObject, P extends IBusinessObject> extends ArrayList<E>
		implements IBusinessObjects<E, P> {

	private static final long serialVersionUID = 7360645136974073845L;

	private PropertyChangeListener propertyListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BusinessObjects.this.getParent()) {
				// 父项属性改变
				BusinessObjects.this.onParentPropertyChanged(evt);
			} else if (BusinessObjects.this.contains(evt.getSource())) {
				// 此集合中的子项的属性改变
				if (evt.getPropertyName() != null && evt.getPropertyName().equals("isDirty")
						&& evt.getNewValue().equals(true)) {
					// 元素状态为Dirty时修改父项状态
					if (BusinessObjects.this.getParent() instanceof Trackable) {
						// 改变父项的状态跟踪
						Trackable trackable = (Trackable) BusinessObjects.this.getParent();
						trackable.markDirty();
					}
				} else {
					if (BusinessObjects.this.getParent() != null && !BusinessObjects.this.getParent().isLoading()) {
						BusinessObjects.this.onItemPropertyChanged(evt);
					}
				}
			} else if (evt.getSource() == BusinessObjects.this && evt.getPropertyName().equals("Size")) {
				if (BusinessObjects.this.parent != null && !BusinessObjects.this.parent.isLoading()) {
					// 集合自身的属性改变事件
					if (BusinessObjects.this.getParent() instanceof Trackable) {
						// 改变父项的状态跟踪
						Trackable trackable = (Trackable) BusinessObjects.this.getParent();
						trackable.markDirty();
					}
				}
			}

		}
	};

	public BusinessObjects() {
		this.setChangeItemStatus(true);
	}

	public BusinessObjects(P parent) {
		this();
		this.setParent(parent);
	}

	private boolean changeItemStatus;

	/**
	 * 是否自动改变子项状态，父项变化
	 * 
	 * @return
	 */
	protected final boolean isChangeItemStatus() {
		return changeItemStatus;
	}

	protected final void setChangeItemStatus(boolean value) {
		this.changeItemStatus = value;
	}

	private P parent = null;

	protected final P getParent() {
		return parent;
	}

	protected final void setParent(P parent) {
		if (this.parent instanceof IBindable) {
			// 移出监听属性改变
			IBindable bindable = (IBindable) this.parent;
			bindable.removeListener(this.propertyListener);
		}
		this.parent = parent;
		if (this.parent instanceof IBindable) {
			// 监听属性改变
			IBindable bindable = (IBindable) this.parent;
			bindable.registerListener(this.propertyListener);
		}
	}

	@Override
	public final boolean add(E item) {
		boolean result = super.add(item);
		if (result) {
			this.afterAddItem(item);
		}
		return result;
	}

	protected void afterAddItem(E item) {
		// 额外逻辑
		if (item instanceof IBindable) {
			// 监听属性改变
			IBindable bindable = (IBindable) item;
			bindable.registerListener(this.propertyListener);
		}
		// 修正子项编号
		if (item instanceof IBOLine) {
			IBOLine line = (IBOLine) item;
			if (line.getLineId() <= 0) {
				int max = 0;
				for (E tmp : this) {
					if (tmp == item) {
						// 自身编号跳过
						continue;
					}
					if (tmp instanceof IBOLine) {
						IBOLine tmpLine = (IBOLine) tmp;
						if (tmpLine.getLineId() > max) {
							max = tmpLine.getLineId();
						}
					}
				}
				line.setLineId(max + 1);
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

	public final boolean remove(E item) {
		boolean result = super.remove(item);
		if (result) {
			this.afterRemoveItem(item);
		}
		return result;
	}

	protected void afterRemoveItem(E item) {
		// 额外逻辑
		if (item instanceof IBindable) {
			// 移出监听属性改变
			IBindable bindable = (IBindable) item;
			bindable.removeListener(this.propertyListener);
		}
	}

	@Override
	public final void delete(E item) {
		if (this.contains(item)) {
			// 集合中的元素
			if (item.isNew()) {
				this.remove(item);
			} else {
				item.delete();
				this.afterRemoveItem(item);
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
		if (this.isChangeItemStatus()) {
			this.changeItemStatus(evt);
		}
	}

	/**
	 * 当元素属性发生变化
	 * 
	 * @param evt
	 */
	protected void onItemPropertyChanged(PropertyChangeEvent evt) {
	}

	/**
	 * 父项数据变化时触发改变元素数据
	 * 
	 * @param evt
	 */
	private void changeItemStatus(PropertyChangeEvent evt) {
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

	@Override
	public ICriteria getElementCriteria() {
		if (this.getParent() == null) {
			return null;
		}
		ICriteria criteria = null;
		if (this.getParent() instanceof IBOMasterData) {
			if (IBOMasterDataLine.class.isAssignableFrom(this.getElementType())) {
				criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME);
				condition.setValue(((IBOMasterData) this.getParent()).getCode());
				condition.setOperation(ConditionOperation.EQUAL);
			}
		} else if (this.getParent() instanceof IBODocument) {
			if (IBODocumentLine.class.isAssignableFrom(this.getElementType())) {
				criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBODocumentLine.MASTER_PRIMARY_KEY_NAME);
				condition.setValue(((IBODocument) this.getParent()).getDocEntry());
				condition.setOperation(ConditionOperation.EQUAL);
			}
		} else if (this.getParent() instanceof IBOSimple) {
			if (IBOSimpleLine.class.isAssignableFrom(this.getElementType())) {
				criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
				condition.setValue(((IBOSimple) this.getParent()).getObjectKey());
				condition.setOperation(ConditionOperation.EQUAL);
			}
		}
		if (criteria == null) {
			criteria = this.getParent().getCriteria();
		}
		if (IBOLine.class.isAssignableFrom(this.getElementType())) {
			// 元素类型是行类型，则添加排序字段
			ISort sort = criteria.getSorts().create();
			sort.setAlias(IBOLine.SECONDARY_PRIMARY_KEY_NAME);
			sort.setSortType(SortType.ASCENDING);
		}
		return criteria;
	}

}
