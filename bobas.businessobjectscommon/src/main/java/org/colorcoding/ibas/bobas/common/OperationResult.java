package org.colorcoding.ibas.bobas.common;

import java.lang.reflect.Array;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.ArrayList;

/**
 * 操作消息结果
 * 
 * @author Niuren.Zhu
 *
 * @param <P> 结果类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "OperationResult", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationResult", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class OperationResult<P> extends OperationMessage implements IOperationResult<P> {

	private static final long serialVersionUID = 3450498667313147437L;

	public OperationResult() {
		super();
	}

	public OperationResult(int resultCode, String message) {
		super(resultCode, message);
	}

	public OperationResult(Exception exception) {
		super(exception);
	}

	public OperationResult(IOperationResult<?> result) {
		this();
		this.copy(result);
	}

	@XmlElement(name = "ResultObject")
	@XmlElementWrapper(name = "ResultObjects")
	private ArrayList<P> resultObjects = null;

	@Override
	public final ArrayList<P> getResultObjects() {
		if (this.resultObjects == null) {
			this.resultObjects = new ArrayList<P>();
		}
		return this.resultObjects;
	}

	public final OperationResult<P> addResultObjects(Iterable<P> values) {
		if (values != null) {
			if (values instanceof Collection) {
				if (this.resultObjects == null) {
					this.resultObjects = new ArrayList<>((Collection<P>) values);
				} else {
					this.getResultObjects().addAll((Collection<P>) values);
				}
			} else {
				this.getResultObjects().addAll(values);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public final OperationResult<P> addResultObjects(Object value) {
		if (value != null) {
			if (value.getClass().isArray()) {
				int length = Array.getLength(value);
				if (this.resultObjects == null) {
					this.resultObjects = new ArrayList<P>(length);
				}
				for (int i = 0; i < length; i++) {
					this.getResultObjects().add((P) Array.get(value, i));
				}
			} else if (value instanceof Iterable) {
				this.addResultObjects((Iterable<P>) value);
			} else {
				this.getResultObjects().add((P) value);
			}
		}
		return this;
	}

	@XmlElementWrapper(name = "Informations")
	@XmlElement(name = "Information", type = OperationInformation.class)
	private ArrayList<IOperationInformation> informations = null;

	@Override
	public final ArrayList<IOperationInformation> getInformations() {
		if (this.informations == null) {
			this.informations = new ArrayList<IOperationInformation>();
		}
		return this.informations;
	}

	public final OperationResult<P> addInformations(String name, String content, String tag) {
		this.getInformations().add(new OperationInformation(name, content, tag));
		return this;
	}

	public final OperationResult<P> addInformations(String name, String content) {
		this.getInformations().add(new OperationInformation(name, content));
		return this;
	}

	public final OperationResult<P> addInformations(IOperationInformation value) {
		if (value != null) {
			this.getInformations().add(value);
		}
		return this;
	}

	public final OperationResult<P> addInformations(Iterable<IOperationInformation> values) {
		if (values != null) {
			this.getInformations().addAll(values);
		}
		return this;
	}

	@Override
	public OperationResult<P> copy(IOperationResult<?> content) {
		if (content != null) {
			super.copy(content);
			this.addResultObjects(content.getResultObjects());
			this.addInformations(content.getInformations());
		}
		return this;
	}

	@Override
	public String toString() {
		return String.format("{operation result: %s - %s}", this.getResultCode(), this.getMessage());
	}
}
