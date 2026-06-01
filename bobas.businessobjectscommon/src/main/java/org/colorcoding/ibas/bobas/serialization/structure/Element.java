package org.colorcoding.ibas.bobas.serialization.structure;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 结构元素基类
 *
 * @author Niuren.Zhu
 *
 */
public abstract class Element extends Serializable implements Comparable<Element> {

	private static final long serialVersionUID = 6706265062886496165L;

	private String name;

	@XmlElement(name = "Name")
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	private String wrapper;

	@XmlElement(name = "Wrapper")
	public final String getWrapper() {
		return wrapper;
	}

	public final void setWrapper(String wrapper) {
		this.wrapper = wrapper;
	}

	/**
	 * 是否集合元素，wrapper非空时为集合
	 *
	 * @return true集合，false非集合
	 */
	public boolean isCollection() {
		if (this.wrapper == null) {
			return false;
		}
		if (this.wrapper.isEmpty()) {
			return false;
		}
		return true;
	}

	private Class<?> type;

	public final Class<?> getType() {
		return type;
	}

	public final void setType(Class<?> type) {
		this.type = type;
	}

	@XmlElement(name = "Type")
	public final String getTypeName() {
		return this.getType().getName();
	}

	public final void setTypeName(String type) throws ClassNotFoundException {
		this.setType(BOFactory.loadClass(type));
	}

	private Element parent;

	@XmlElement(name = "Parent")
	public final Element getParent() {
		return parent;
	}

	public final void setParent(Element parent) {
		this.parent = parent;
	}

	private Elements childs;

	@XmlElementWrapper(name = "Childs")
	@XmlElement(name = "Element", type = Element.class)
	public final Elements getChilds() {
		if (this.childs == null) {
			this.childs = new Elements(this);
		}
		return childs;
	}

	public final void setChilds(Elements childs) {
		this.childs = childs;
	}

	/** 获取元素层级深度，根元素为0 */
	public int getLevel() {
		if (this.getParent() == null) {
			return 0;
		}
		return this.getParent().getLevel() + 1;
	}

	/**
	 * 比较排序顺序：大写字母开头的元素排在前面（优先输出），同组内按名称自然排序
	 *
	 * @param target 目标元素
	 * @return 比较结果
	 */
	@Override
	public int compareTo(Element target) {
		String sName = this.getWrapper();
		if (sName == null || sName.isEmpty()) {
			sName = this.getName();
		}
		String tName = target.getWrapper();
		if (tName == null || tName.isEmpty()) {
			tName = target.getName();
		}
		if (sName == null || sName.isEmpty() || tName == null || tName.isEmpty()) {
			return 0;
		}
		if (Character.isUpperCase(tName.charAt(0)) == Character.isUpperCase(sName.charAt(0))) {
			return sName.compareTo(tName);
		} else {
			if (Character.isUpperCase(tName.charAt(0))) {
				return -1;
			}
			return 1;
		}
	}

	@Override
	public String toString() {
		if (this.getType() != null) {
			return String.format("{element: %s %s}", this.getWrapper() != null ? this.getWrapper() : this.getName(),
					this.getType().getSimpleName());
		}
		return String.format("{element: %s}", this.getWrapper() != null ? this.getWrapper() : this.getName());
	}
}