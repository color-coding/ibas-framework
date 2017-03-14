package org.colorcoding.ibas.bobas.commands;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.colorcoding.ibas.bobas.i18n.i18n;

/**
 * 命令
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class Command<C> {

	/**
	 * 帮助命令
	 */
	public static final String ARGUMENT_NAME_HELP = "-help";
	/**
	 * 返回值，-10，没有错误，但未找到对应的命令符
	 */
	public static final int RETURN_VALUE_NOT_FOUND_COMMAND_PROMPT = -10;
	/**
	 * 返回值，-1，没有错误，但核心命令未执行
	 */
	public static final int RETURN_VALUE_NO_COMMAND_EXECUTION = -1;
	/**
	 * 返回值，0，没有错误，核心命令执行
	 */
	public static final int RETURN_VALUE_SUCCESS = 0;
	/**
	 * 返回值，22，错误，无效的参数
	 */
	public static final int RETURN_VALUE_INVALID_ARGUMENT = 22;
	/**
	 * 返回值，1，错误，运行命令失败
	 */
	public static final int RETURN_VALUE_COMMAND_EXECUTION_FAILD = 1;
	/**
	 * 新行字符
	 */
	public static final String NEW_LINE = System.getProperty("line.separator", "\r\n");

	private String name;

	public String getName() {
		if (this.name == null) {
			this.name = "ibas|commands";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		System.out.println(String.format("[commands|%s]: %s", this.getName(), String.format(message, args)));
	}

	/**
	 * 打印异常
	 * 
	 * @param error
	 *            异常
	 */
	protected void print(Throwable error) {
		try {
			ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
			error.printStackTrace(new java.io.PrintWriter(buf, true));
			String message = buf.toString();
			buf.close();
			this.print(message);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * 创建此命令的参数
	 * 
	 * @return 此命令所有参数的实例数组
	 */
	protected abstract Argument[] createArguments();

	/**
	 * 运行
	 * 
	 * @param args
	 *            参数
	 * @return
	 */
	public final int run(String[] args) {
		// 包含帮助命名，打印帮助信息后退出
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().startsWith(ARGUMENT_NAME_HELP)) {
				this.printHelps();
				return RETURN_VALUE_NO_COMMAND_EXECUTION;
			}
		}
		Argument[] arguments = this.createArguments();
		if (arguments != null) {
			for (int i = 0; i < args.length; i++) {
				boolean done = false;
				for (Argument argument : arguments) {
					if (argument.check(args[i])) {
						// 匹配的参数
						argument.setOriginal(args[i]);
						done = true;
					}
				}
				if (!done) {
					// 未能识别的参数
					this.print(i18n.prop("msg_bobas_commands_invalid_argument"), args[i]);
					return RETURN_VALUE_INVALID_ARGUMENT;
				}
			}
		}
		if (arguments == null) {
			arguments = new Argument[] {};
		}
		if (this.isRequiredArguments()) {
			// 要求输出参数，有则运行，没有则显示帮助
			boolean done = false;// 是否输出了参数
			for (Argument argument : arguments) {
				if (argument.isInputed()) {
					done = true;
					break;
				}
			}
			if (done) {
				return this.run(arguments);
			}
			this.printHelps();
			return RETURN_VALUE_NO_COMMAND_EXECUTION;
		}
		return this.run(arguments);
	}

	/**
	 * 打印帮助信息
	 * 
	 * @param arguments
	 */
	protected void printHelps() {
		Argument[] arguments = this.createArguments();
		StringBuilder stringBuilder = new StringBuilder();
		if (this.getDescription() != null && !this.getDescription().isEmpty())
			stringBuilder.append(this.getDescription());
		for (Argument argument : arguments) {
			stringBuilder.append(NEW_LINE);
			stringBuilder.append("    ");
			stringBuilder.append(argument.getName());
			for (int i = argument.getName().length(); i < 30; i++) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(argument.getDescription());
		}
		stringBuilder.append(NEW_LINE);
		stringBuilder.append(NEW_LINE);
		this.moreHelps(stringBuilder);
		this.print(stringBuilder.toString());
	}

	/**
	 * 更多的帮助信息
	 * 
	 * @param stringBuilder
	 */
	protected void moreHelps(StringBuilder stringBuilder) {

	}

	/**
	 * 是否要求提供参数
	 * 
	 * true：没参数自动显示帮助；false：没参数也运行（run）
	 * 
	 * @return
	 */
	protected abstract boolean isRequiredArguments();

	/**
	 * 运行
	 * 
	 * @param arguments
	 *            参数
	 * @return
	 */
	protected abstract int run(Argument[] arguments);

	/**
	 * 创建参数
	 * 
	 * @param values
	 *            参数字符串
	 * @return
	 */
	protected Collection<Parameter> createParameters(String values) {
		ArrayList<Parameter> parameters = new ArrayList<>();
		if (values != null && !values.isEmpty()) {
			values = values.trim();
			if (values.startsWith("[") && values.endsWith("]")) {
				ArrayList<String> tmpValues = new ArrayList<>();
				StringBuilder stringBuilder = null;
				char[] charValues = values.toCharArray();
				for (int i = 1; i < charValues.length - 1; i++) {
					if (charValues[i] == '{') {
						stringBuilder = new StringBuilder();
					}
					if (stringBuilder != null) {
						stringBuilder.append(charValues[i]);
					}
					if (charValues[i] == '}') {
						tmpValues.add(stringBuilder.toString());
						stringBuilder = null;
					}
				}
				for (String string : tmpValues) {
					Parameter parameter = Parameter.create(string);
					if (parameter != null) {
						parameters.add(parameter);
					}
				}
			} else if (values.startsWith("{") && values.endsWith("}")) {
				Parameter parameter = Parameter.create(values);
				if (parameter != null) {
					parameters.add(parameter);
				}
			}
		}
		return parameters;
	}
}
