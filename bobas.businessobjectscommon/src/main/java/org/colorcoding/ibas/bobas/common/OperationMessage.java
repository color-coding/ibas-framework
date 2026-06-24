package org.colorcoding.ibas.bobas.common;

import java.util.IdentityHashMap;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.List;
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

	private static final long serialVersionUID = -4506576628874995959L;

	public OperationMessage() {
		super();
		this.setSignID(UUID.randomUUID().toString());
		this.setTime(DateTimes.now());
	}

	public OperationMessage(int resultCode, String message) {
		super(resultCode, message);
		this.setSignID(UUID.randomUUID().toString());
		this.setTime(DateTimes.now());
	}

	public OperationMessage(Exception exception) {
		this();
		this.setError(exception);
	}

	public OperationMessage(IOperationMessage result) {
		this();
		this.copy(result);
	}

	@XmlElement(name = "SignID")
	private String signID;

	@Override
	public final String getSignID() {
		return this.signID;
	}

	public final void setSignID(String value) {
		this.signID = value;
	}

	@XmlElement(name = "Time")
	private DateTime time;

	@Override
	public final DateTime getTime() {
		return this.time;
	}

	public final void setTime(DateTime value) {
		this.time = value;
	}

	@XmlElement(name = "UserSign")
	private String userSign;

	@Override
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
			if (this.getMessage() == null || this.getMessage().isEmpty()) {
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
		if (this.error == null) {
			return;
		}
		if (this.getResultCode() == 0) {
			this.setResultCode(-1);
		}
		// 已有 message，不覆盖
		String currentMessage = this.getMessage();
		if (currentMessage != null && !currentMessage.isEmpty()) {
			return;
		}
		if (MyConfiguration.isDebugMode()) {
			// debug 模式保留多层错误信息
			this.setMessage(this.buildDebugMessage(this.error));
		} else {
			// 非 debug 模式仅取顶层消息，精简展示避免泄露内部信息
			String message = this.error.getMessage();
			if (message == null || message.isEmpty()) {
				this.setMessage("internal error");
			} else {
				if (message.length() > MAX_MESSAGE_LENGTH) {
					message = message.substring(0, MAX_MESSAGE_LENGTH);
				}
				this.setMessage(message);
			}
		}
	}

	/**
	 * cause 链最大遍历深度
	 */
	private static final int MAX_CAUSE_DEPTH = 5;

	/**
	 * 消息最大长度
	 */
	private static final int MAX_MESSAGE_LENGTH = 1024;

	/**
	 * 多层错误消息之间的分隔符
	 */
	private static final String MESSAGE_SEPARATOR = "\n";

	/**
	 * 构建调试模式下的多层错误描述。
	 * <ul>
	 * <li>沿 cause 链向下遍历，最多 {@value #MAX_CAUSE_DEPTH} 层</li>
	 * <li>使用 IdentityHashMap 防止自引用造成的死循环</li>
	 * <li>若内层 message 是外层 message 的后缀（外层只是简单包了内层），跳过外层避免重复</li>
	 * <li>消息为空时使用异常类名兜底</li>
	 * <li>各层使用 {@value #MESSAGE_SEPARATOR} 分隔</li>
	 * </ul>
	 *
	 * @param root 根异常
	 * @return 多层错误描述
	 */
	private String buildDebugMessage(Throwable root) {
		IdentityHashMap<Throwable, Boolean> visited = new IdentityHashMap<>();
		List<String> messages = new ArrayList<>(MAX_CAUSE_DEPTH);
		Throwable current = root;
		while (current != null && messages.size() < MAX_CAUSE_DEPTH && visited.put(current, Boolean.TRUE) == null) {
			String msg = current.getMessage();
			if (msg == null || msg.isEmpty()) {
				msg = current.getClass().getName();
			}
			messages.add(msg);
			current = current.getCause();
		}
		StringBuilder stringBuilder = new StringBuilder(256);
		for (int i = 0; i < messages.size(); i++) {
			String message = messages.get(i);
			int next = i + 1;
			if (next < messages.size()) {
				String nMessage = messages.get(next);
				// 内层完全是外层的后缀，外层无额外信息，跳过外层
				if (nMessage != null && !nMessage.isEmpty() && message.endsWith(nMessage)) {
					continue;
				}
			}
			if (stringBuilder.length() > 0) {
				stringBuilder.append(MESSAGE_SEPARATOR);
			}
			stringBuilder.append(message);
		}
		return stringBuilder.length() > 0 ? stringBuilder.toString() : root.getClass().getName();
	}

	/**
	 * 复制数据
	 * 
	 * @param content 复制内容
	 * @return 当前实例
	 */
	@Override
	public OperationMessage copy(IOperationMessage content) {
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
		return String.format("{opMessage: %s - %s}", this.getResultCode(), this.getMessage());
	}
}
