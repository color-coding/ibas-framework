package org.colorcoding.ibas.bobas.core;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class Serializable implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * （系统）回掉方法-反序列化之前
	 * 
	 * @param target
	 * @param parent
	 */
	final void beforeUnmarshal(Unmarshaller target, Object parent) {
		this.beforeUnmarshal(parent);
	}

	/**
	 * （系统）回掉方法-反序列化之后
	 * 
	 * @param target
	 * @param parent
	 */
	final void afterUnmarshal(Unmarshaller target, Object parent) {
		this.afterUnmarshal(parent);
	}

	/**
	 * （系统）回掉方法-序列化之前
	 * 
	 * @param target
	 * @param parent
	 */
	final void beforeMarshal(Marshaller marshaller) {
		this.beforeMarshal();
	}

	/**
	 * （系统）回掉方法-序列化之后
	 * 
	 * @param target
	 * @param parent
	 */
	final void afterMarshal(Marshaller marshaller) {
		this.afterMarshal();
	}

	/**
	 * 反序列化之前调用
	 * 
	 * @param parent 所属父项
	 */
	protected void beforeUnmarshal(Object parent) {
	}

	/**
	 * 反序列化之后调用
	 * 
	 * @param parent 所属父项
	 */
	protected void afterUnmarshal(Object parent) {
	}

	/**
	 * 序列化之前调用
	 */
	protected void beforeMarshal() {
	}

	/**
	 * 序列化之后调用
	 */
	protected void afterMarshal() {
	}
}
