package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 操作消息结果
 * 
 * @author Niuren.Zhu
 *
 * @param <P>
 *            结果类型
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "OperationResult", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationResult", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public class OperationResult<P> extends OperationMessages implements IOperationResult<P> {

	/**
	 * 
	 */
	public OperationResult() {
		super();
	}

	/**
	 * @param exception
	 */
	public OperationResult(Exception exception) {
		super(exception);
	}

	/**
	 * @param userSign
	 * @param exception
	 */
	public OperationResult(String userSign, Exception exception) {
		super(userSign, exception);
	}

	/**
	 * @param userSign
	 * @param message
	 */
	public OperationResult(String userSign, String message) {
		super(userSign, message);
	}

	/**
	 * @param userSign
	 */
	public OperationResult(String userSign) {
		super(userSign);
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

	public final void setResultObjects(ArrayList<P> value) {
		this.resultObjects = value;
	}

	public final void addResultObjects(Iterable<?> values) {
		if (this.resultObjects == null) {
			this.resultObjects = new ArrayList<P>();
		}
		if (values != null) {
			for (Object object : values) {
				this.addResultObjects(object);
			}
		}
	}

	public final void addResultObjects(Object[] values) {
		if (this.resultObjects == null) {
			this.resultObjects = new ArrayList<P>();
		}
		if (values != null) {
			for (Object object : values) {
				this.addResultObjects(object);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public final void addResultObjects(Object value) {
		if (this.resultObjects == null) {
			this.resultObjects = new ArrayList<P>();
		}
		if (value != null) {
			this.resultObjects.add((P) value);
		}
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

	public final void setInformations(ArrayList<IOperationInformation> value) {
		this.informations = value;
	}

	public final void addInformations(IOperationInformation value) {
		if (this.informations == null) {
			this.informations = new ArrayList<IOperationInformation>();
		}
		if (value != null) {
			this.informations.add(value);
		}
	}

	public final void addInformations(Iterable<IOperationInformation> values) {
		if (this.informations == null) {
			this.informations = new ArrayList<IOperationInformation>();
		}
		if (values != null) {
			this.informations.addAll(values);
		}
	}

	public final void addInformations(IOperationInformation[] values) {
		if (this.informations == null) {
			this.informations = new ArrayList<IOperationInformation>();
		}
		if (values != null) {
			this.informations.addAll(values);
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
