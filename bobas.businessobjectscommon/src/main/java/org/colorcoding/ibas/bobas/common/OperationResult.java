package org.colorcoding.ibas.bobas.common;

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
 * @param <P>
 *            结果类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "OperationResult", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationResult", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class OperationResult<P> extends OperationMessage implements IOperationResult<P> {

	public OperationResult() {
		super();
	}

	public OperationResult(Exception exception) {
		super(exception);
	}

	public OperationResult(IOperationResult<?> result) {
		this.copy(result);
	}

	private ArrayList<P> resultObjects = null;

	@Override
	@XmlElementWrapper(name = "ResultObjects")
	@XmlElement(name = "ResultObject")
	public final ArrayList<P> getResultObjects() {
		if (this.resultObjects == null) {
			this.resultObjects = new ArrayList<P>();
		}
		return this.resultObjects;
	}

	public final void addResultObjects(Iterable<?> values) {
		if (values == null) {
			return;
		}
		for (Object value : values) {
			this.addResultObjects(value);
		}
	}

	public final void addResultObjects(Object[] values) {
		if (values == null) {
			return;
		}
		for (Object value : values) {
			this.addResultObjects(value);
		}
	}

	@SuppressWarnings("unchecked")
	public final void addResultObjects(Object value) {
		if (value == null) {
			return;
		}
		this.getResultObjects().add((P) value);
	}

	private ArrayList<IOperationInformation> informations = null;

	@Override
	@XmlElementWrapper(name = "Informations")
	@XmlElement(name = "Information", type = OperationInformation.class)
	public final ArrayList<IOperationInformation> getInformations() {
		if (this.informations == null) {
			this.informations = new ArrayList<IOperationInformation>();
		}
		return this.informations;
	}

	public final void addInformations(String name, String content, String tag) {
		this.getInformations().add(new OperationInformation(name, content, tag));
	}

	public final void addInformations(String name, String content) {
		this.getInformations().add(new OperationInformation(name, content));
	}

	public final void addInformations(IOperationInformation value) {
		if (value == null) {
			return;
		}
		this.getInformations().add(value);
	}

	public final void addInformations(Iterable<IOperationInformation> values) {
		if (values == null) {
			return;
		}
		for (IOperationInformation value : values) {
			this.addInformations(value);
		}
	}

	public final void addInformations(IOperationInformation[] values) {
		if (values == null) {
			return;
		}
		for (IOperationInformation value : values) {
			this.addInformations(value);
		}
	}

	@Override
	public void copy(IOperationResult<?> opRslt) {
		if (opRslt == null) {
			return;
		}
		super.copy(opRslt);
		this.addResultObjects(opRslt.getResultObjects());
		this.addInformations(opRslt.getInformations());
	}

}
