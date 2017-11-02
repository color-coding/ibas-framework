package org.colorcoding.ibas.bobas.common;

import java.util.UUID;

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
@XmlType(name = "OperationMessage", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "OperationMessage", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class OperationMessage extends Result implements IOperationMessage {

	public OperationMessage() {
		this.setSignID(UUID.randomUUID().toString());
		this.setResultCode(0);
		this.setTime(DateTime.getNow());
	}

	public OperationMessage(String message) {
		this();
		this.setUserSign(userSign);
		this.setMessage(message);
	}

	public OperationMessage(Exception exception) {
		this();
		this.setError(exception);
	}

	public OperationMessage(IOperationMessage result) {
		this();
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

	private DateTime time;

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
		if (this.error == null && this.getResultCode() != 0 && this.getMessage() != null
				&& !this.getMessage().isEmpty()) {
			// 发生错误，有描述，自动创建异常对象
			this.error = new Exception(this.getMessage());
		}
		return this.error;
	}

	public final void setError(Exception value) {
		this.error = value;
		if (this.error != null) {
			if (this.getResultCode() == 0) {
				this.setResultCode(-1);
			}
			if (this.getMessage() == null || this.getMessage().isEmpty()) {
				this.setMessage(String.format("%s: %s", this.error.getClass().getName(), this.error.getMessage()));
			}
		}
	}

	@Override
	public void copy(IOperationMessage opMsg) {
		this.setSignID(opMsg.getSignID());
		this.setUserSign(opMsg.getUserSign());
		this.setTime(opMsg.getTime());
		this.setResultCode(opMsg.getResultCode());
		this.setMessage(opMsg.getMessage());
		this.setError(opMsg.getError());
	}

	@Override
	public String toString() {
		return String.format("{operation message: %s - %s}", this.getResultCode(), this.getMessage());
	}
}
