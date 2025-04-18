package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.Serializable;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 结果
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Result", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Result", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class Result extends Serializable implements IResult {

	private static final long serialVersionUID = 4221626741557277709L;

	public Result() {
		this.setResultCode(0);
	}

	public Result(int resultCode, String message) {
		this();
		this.setResultCode(resultCode);
		this.setMessage(message);
	}

	@XmlElement(name = "ResultCode")
	private int resultCode;

	@Override
	public final int getResultCode() {
		return this.resultCode;
	}

	public final void setResultCode(int value) {
		this.resultCode = value;
		this.message = null;
		if (this.resultCode == 0 && this.message == null) {
			this.setMessage(I18N.prop("msg_bobas_operation_successful"));
		}
	}

	@XmlElement(name = "Message")
	private String message;

	@Override
	public final String getMessage() {
		return this.message;
	}

	public final void setMessage(String value) {
		this.message = value;
	}

	@Override
	public String toString() {
		return String.format("{result: %s - %s}", this.getResultCode(), this.getMessage());
	}
}
