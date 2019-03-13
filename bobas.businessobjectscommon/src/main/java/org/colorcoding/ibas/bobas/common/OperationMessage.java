package org.colorcoding.ibas.bobas.common;

import java.util.ArrayList;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.i18n.I18N;

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

	private static final int ERROR_DEEP = 5;
	private static final long serialVersionUID = -4506576628874995959L;

	public OperationMessage() {
		super();
		this.setSignID(UUID.randomUUID().toString());
		this.setTime(DateTime.getNow());
	}

	public OperationMessage(int resultCode, String message) {
		super(resultCode, message);
		this.setSignID(UUID.randomUUID().toString());
		this.setTime(DateTime.getNow());
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
		if (this.error == null && this.getResultCode() != 0) {
			// 发生错误，有描述，自动创建异常对象
			if (this.getMessage() == null || !this.getMessage().isEmpty()) {
				// 没有描述，未知错误
				this.error = new Exception(I18N.prop("msg_bobas_unknown_exception"));
			} else {
				// 描述错误
				this.error = new Exception(this.getMessage());
			}
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
				Throwable error = this.error;
				ArrayList<String> errMessages = new ArrayList<>(ERROR_DEEP);
				while (error != null && errMessages.size() <= ERROR_DEEP) {
					errMessages.add(error.getMessage());
					error = error.getCause();
				}
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < errMessages.size(); i++) {
					String message = errMessages.get(i);
					if (message == null) {
						continue;
					}
					int next = i + 1;
					if (next < errMessages.size()) {
						String nMessage = errMessages.get(next);
						if (nMessage != null && !nMessage.isEmpty() && message.endsWith(nMessage)) {
							continue;
						}
					}
					stringBuilder.append(message);
				}
				if (stringBuilder.length() > 0) {
					this.setMessage(stringBuilder.toString());
				} else {
					this.setMessage(this.error.getClass().getName());
				}
			}
		}
	}

	/**
	 * 复制数据
	 * 
	 * @param content 复制内容
	 * @return 当前实例
	 */
	@Override
	public IOperationMessage copy(IOperationMessage content) {
		this.setSignID(content.getSignID());
		this.setUserSign(content.getUserSign());
		this.setTime(content.getTime());
		this.setResultCode(content.getResultCode());
		this.setMessage(content.getMessage());
		this.setError(content.getError());
		return this;
	}

	@Override
	public String toString() {
		return String.format("{operation message: %s - %s}", this.getResultCode(), this.getMessage());
	}
}
