package org.colorcoding.ibas.bobas.test.commands;

import java.util.ArrayList;

import org.colorcoding.ibas.bobas.commands.Argument;
import org.colorcoding.ibas.bobas.commands.Command;
import org.colorcoding.ibas.bobas.commands.Prompt;

@Prompt(Command4Test.COMMAND_PROMPT)
public class Command4Test extends Command<Command4Test> {

	public Command4Test() {
		this.setName(COMMAND_PROMPT);
		this.setDescription("测试命令");
	}

	/**
	 * 命令符
	 */
	public final static String COMMAND_PROMPT = "test";

	@Override
	protected boolean isRequiredArguments() {
		return true;// 有参数才调用
	}

	@Override
	protected Argument[] createArguments() {
		ArrayList<Argument> arguments = new ArrayList<>();
		// 添加自身参数
		arguments.add(new Argument("-SqlFile", "使用的SQL文件"));
		arguments.add(new Argument("-Company", "公司标记，用于数据库对象前缀"));
		arguments.add(new Argument("-DbServer", "数据库地址"));
		arguments.add(new Argument("-DbPort", "数据库端口"));
		arguments.add(new Argument("-DbSchema", "应用架构"));
		arguments.add(new Argument("-DbName", "数据库名称"));
		arguments.add(new Argument("-DbUser", "数据库用户"));
		arguments.add(new Argument("-DbPassword", "数据库用户密码"));
		return arguments.toArray(new Argument[] {});
	}

	/**
	 * 为帮助添加调用代码的示例
	 */
	@Override
	protected void moreHelps(StringBuilder stringBuilder) {
		stringBuilder.append("示例：");
		stringBuilder.append(NEW_LINE);
		stringBuilder.append("  ");
		stringBuilder.append(COMMAND_PROMPT);
		stringBuilder.append(" ");
		stringBuilder.append("-SqlFile=D:\\sql_mysql_ibas_initialization.xml");
		stringBuilder.append(" ");
		stringBuilder.append("-Company=CC");
		stringBuilder.append(" ");
		stringBuilder.append("-DbServer=ibas-dev-mysql");
		stringBuilder.append(" ");
		stringBuilder.append("-DbPort=3306");
		stringBuilder.append(" ");
		stringBuilder.append("-DbSchema=");
		stringBuilder.append(" ");
		stringBuilder.append("-DbName=ibas_demo");
		stringBuilder.append(" ");
		stringBuilder.append("-DbUser=root");
		stringBuilder.append(" ");
		stringBuilder.append("-DbPassword=1q2w3e");
		super.moreHelps(stringBuilder);
	}

	@Override
	protected int run(Argument[] arguments) {
		return 0;
	}

}
