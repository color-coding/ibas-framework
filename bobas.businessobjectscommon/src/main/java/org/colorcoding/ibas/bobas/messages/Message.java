package org.colorcoding.ibas.bobas.messages;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.util.StringBuilder;

public class Message implements IMessage {

	public static IMessage create() {
		return new Message();
	}

	public static IMessage create(String message) {
		return new Message(message);
	}

	public static IMessage create(MessageLevel level, String message) {
		return new Message(level, message);
	}

	public static IMessage create(MessageLevel level, String message, String tag) {
		return new Message(level, message, tag);
	}

	public Message() {
		this.setTime(DateTime.getNow());
	}

	public Message(String message) {
		this(MessageLevel.information, message);
	}

	public Message(MessageLevel level, String message) {
		this(level, message, "");
	}

	public Message(MessageLevel level, String message, String tag) {
		this();
		this.setLevel(level);
		this.setContent(message);
		this.setTag(tag);
	}

	DateTime time;

	@Override
	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

	MessageLevel level;

	@Override
	public MessageLevel getLevel() {
		if (this.level == null)
			this.level = MessageLevel.information;
		return level;
	}

	public void setLevel(MessageLevel level) {
		this.level = level;
	}

	String content;

	@Override
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	String tag;

	@Override
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String outString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getTime().toString("yyyy-MM-dd HH:mm:ss.SSS"));
		stringBuilder.append(" ");
		stringBuilder.append(this.getLevel().toString());
		stringBuilder.append(" ");
		stringBuilder.append(this.getTag());
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append(this.getContent());
		return stringBuilder.toString();
	}

}
