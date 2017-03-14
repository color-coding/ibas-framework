package org.colorcoding.ibas.bobas.test.commands;

import java.util.ArrayList;

import org.colorcoding.ibas.bobas.commands.CommandsManager;

import junit.framework.TestCase;

public class testCommands extends TestCase {

	public void testCommand4Test() {
		CommandsManager commandsManager = new CommandsManager();
		commandsManager.register(Command4Test.class);
		ArrayList<String> args = new ArrayList<>();
		args.add(String.format(Command4Test.COMMAND_PROMPT)); // 命令
		args.add(String.format("-TemplateFolder=%s", "eclipse/ibas_classic")); // 使用的模板
		args.add(String.format("-OutputFolder=%s", "/home/manager/code/btulz4ibcp/out/")); // 输出目录
		args.add(String.format("-GroupId=%s", "org.colorcoding"));// 组标记
		args.add(String.format("-ArtifactId=%s", "ibas"));// 项目标记
		args.add(String.format("-ProjectVersion=%s", "0.0.1"));// 项目版本
		args.add(String.format("-ProjectUrl=%s", "http://colorcoding.org"));// 项目地址
		args.add(String.format("-Domains=%s",
				"/home/manager/code/btulz.transforms/btulz.transforms.shell/target/test-classes")); // 模型文件
		args.add(String.format("-Parameters=%s",
				"[{\"name\":\"Company\",\"value\":\"CC\"},{\"name\":\"ibasVersion\",\"value\":\"0.1.1\"},{\"name\":\"jerseyVersion\",\"value\":\"2.22.1\"}]")); // 其他参数
		System.out.println("显示帮助信息：");
		commandsManager.run(new String[] { Command4Test.COMMAND_PROMPT, Command4Test.ARGUMENT_NAME_HELP });
		System.out.println("开始运行：");
		commandsManager.run(args.toArray(new String[] {}));
	}

}
