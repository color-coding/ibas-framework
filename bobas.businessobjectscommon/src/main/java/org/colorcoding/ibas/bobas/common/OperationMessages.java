package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 操作消息
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "OperationMessages", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationMessages", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class OperationMessages extends Result implements IOperationMessages {

	public OperationMessages() {
		this.setSignID(java.util.UUID.randomUUID().toString());
		this.setResultCode(0);
	}

	public OperationMessages(String userSign) {
		this();
		this.setUserSign(userSign);
	}

	public OperationMessages(String userSign, String message) {
		this();
		this.setUserSign(userSign);
		this.setMessage(message);
	}

	public OperationMessages(String userSign, Exception exception) {
		this();
		this.setUserSign(userSign);
		this.setError(exception);
	}

	public OperationMessages(Exception exception) {
		this();
		this.setError(exception);
	}

	public OperationMessages(IOperationMessages result) {
		this.copy(result);
	}

	private String signID;

	@Override
	@XmlElement(name = "SignID")
	public final String getSignID() {
		return this.signID;
	}

	public final void setSignID(String value) {
		this.signID = value;
	}

	private DateTime time = DateTime.getNow();

	@Override
	@XmlElement(name = "Time")
	public final DateTime getTime() {
		return this.time;
	}

	public final void setTime(DateTime value) {
		this.time = value;
	}

	private String userSign;

	@Override
	@XmlElement(name = "UserSign")
	public final String getUserSign() {
		return this.userSign;
	}

	public final void setUserSign(String value) {
		this.userSign = value;
	}

	private Exception error;

	@Override
	public final Exception getError() {
		return this.error;
	}

	public final void setError(Exception value) {
		this.error = value;
		if (this.error != null) {
			if (this.getResultCode() == 0) {
				this.setResultCode(-1);
			}
			if (this.getMessage() == null || this.getMessage().isEmpty()) {
				this.setMessage(this.error.getMessage());
			}
		}

	}

	@Override
	public String toString() {
		return String.format("{operation messages: %s - %s}", this.getResultCode(), this.getMessage());
	}

	@Override
	public void copy(IOperationMessages opMsg) {
		this.setSignID(opMsg.getSignID());
		this.setUserSign(opMsg.getUserSign());
		this.setTime(opMsg.getTime());
		this.setResultCode(opMsg.getResultCode());
		this.setMessage(opMsg.getMessage());
		this.setError(opMsg.getError());
	}
}
