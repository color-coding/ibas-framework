package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlType(name = "ComputeException", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class ComputeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3222542562501337569L;

	public ComputeException() {
	}

	public ComputeException(String message) {
		super(message);
	}

	public ComputeException(String message, Throwable exception) {
		super(message, exception);
	}

	public ComputeException(Throwable exception) {
		super(exception);
	}
}
