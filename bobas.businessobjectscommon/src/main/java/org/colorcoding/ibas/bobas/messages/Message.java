package org.colorcoding.ibas.bobas.messages;

import java.io.IOException;
import java.io.Writer;

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
        this(MessageLevel.INFO, message);
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
            this.level = MessageLevel.INFO;
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

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String outString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(this.getLevel().toString());
        stringBuilder.append("]");
        stringBuilder.append(" ");
        stringBuilder.append("[");
        stringBuilder.append(this.getTime().toString("yyyy-MM-dd HH:mm:ss.SSS"));
        stringBuilder.append("]");
        if (this.getTag() != null && !this.getTag().isEmpty()) {
            stringBuilder.append(" ");
            stringBuilder.append("[");
            stringBuilder.append(this.getTag());
            stringBuilder.append("]");
        }
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append(this.getContent());
        // stringBuilder.append(LINE_SEPARATOR);
        return stringBuilder.toString();
    }

    @Override
    public void outString(Writer writer) throws IOException {
        writer.append("[");
        writer.append(this.getLevel().toString());
        writer.append("]");
        writer.append(" ");
        writer.append("[");
        writer.append(this.getTime().toString("yyyy-MM-dd HH:mm:ss.SSS"));
        writer.append("]");
        if (this.getTag() != null && !this.getTag().isEmpty()) {
            writer.append(" ");
            writer.append("[");
            writer.append(this.getTag());
            writer.append("]");
        }
        writer.append(LINE_SEPARATOR);
        writer.append(this.getContent());
        writer.append(LINE_SEPARATOR);
    }

}
