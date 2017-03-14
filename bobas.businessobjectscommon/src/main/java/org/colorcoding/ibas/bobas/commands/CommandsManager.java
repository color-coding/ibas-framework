package org.colorcoding.ibas.bobas.commands;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 命令管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class CommandsManager {

	private HashMap<String, Class<? extends Command<?>>> commands;

	protected Map<String, Class<? extends Command<?>>> getCommands() {
		if (this.commands == null) {
			this.commands = new HashMap<>();
		}
		return this.commands;
	}

	protected Class<? extends Command<?>> getCommands(String prompt) {
		for (String name : this.getCommands().keySet()) {
			if (name.equalsIgnoreCase(prompt)) {
				return this.getCommands().get(name);
			}
		}
		return null;
	}

	/**
	 * 注册命令，已存在则替换
	 * 
	 * @param command
	 * @return
	 */
	public boolean register(Class<? extends Command<?>> command) {
		if (command != null) {
			Annotation annotation = command.getAnnotation(Prompt.class);
			if (annotation instanceof Prompt) {
				Prompt prompt = (Prompt) annotation;
				if (prompt.value() != null && !prompt.value().isEmpty()) {
					if (this.getCommands().containsKey(prompt.value())) {
						// 已存在则替换
						this.getCommands().replace(prompt.value(), command);
					} else {
						this.getCommands().put(prompt.value(), command);
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 打印消息
	 * 
	 * @param message
	 *            消息模板
	 * @param args
	 *            参数
	 */
	protected void print(String message, Object... args) {
		System.out.println("[btulz]: " + String.format(message, args));
	}

	public int run(String[] args) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String arg : args) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" ");
			}
			stringBuilder.append("{");
			stringBuilder.append(arg);
			stringBuilder.append("}");
		}
		RuntimeLog.log(MessageLevel.DEBUG, "input in command args %s.", stringBuilder);
		if (args == null || args.length == 0
				|| (args.length == 1 && args[0].trim().equals(Command.ARGUMENT_NAME_HELP))) {
			// 没有提供参数，则输出已注册的所有命令
			stringBuilder = new StringBuilder();
			for (String name : this.getCommands().keySet()) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(" | ");
				}
				stringBuilder.append(name);
			}
			this.print("you can use the command [%s].", stringBuilder);
			return Command.RETURN_VALUE_NO_COMMAND_EXECUTION;
		}
		String prompt = args[0].trim();
		Class<? extends Command<?>> commandType = this.getCommands(prompt);
		if (commandType == null) {
			// 没有找到命令
			this.print("not found command [%s].", prompt);
			return Command.RETURN_VALUE_NOT_FOUND_COMMAND_PROMPT;
		}
		try {
			Command<?> command = commandType.newInstance();
			String[] cArgs = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				cArgs[i - 1] = args[i];
			}
			return command.run(cArgs);
		} catch (InstantiationException | IllegalAccessException e) {
			this.print("call command [%s] faild.", prompt);
			this.print(e.toString());
			return Command.RETURN_VALUE_COMMAND_EXECUTION_FAILD;
		}
	}

}
