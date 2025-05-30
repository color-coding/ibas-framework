package org.colorcoding.ibas.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.task.Daemon;
import org.colorcoding.ibas.bobas.task.IDaemonTask;
import org.colorcoding.ibas.demo.MyConfiguration;

@WebListener
public class AppInitListener implements ServletContextListener {

	private long taskId = 0;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 如果数据库文件不存在则创建
		File dbFile = new File(MyConfiguration.getDataFolder(),
				MyConfiguration.getConfigValue("Master" + MyConfiguration.CONFIG_ITEM_DB_NAME));
		if (dbFile.isFile() && dbFile.exists()) {
			return;
		}
		try {
			this.taskId = Daemon.register(new IDaemonTask() {

				@Override
				public void run() {
					// 任务只执行一次
					long taskId = AppInitListener.this.taskId;
					AppInitListener.this.taskId = 0;
					AppInitListener.this.runCommands();
					Daemon.unRegister(taskId);
				}

				@Override
				public String getName() {
					return "init sqlite db";
				}

				@Override
				public long getInterval() {
					if (AppInitListener.this.taskId > 0) {
						return 1;
					}
					return -1;
				}
			});
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

	/**
	 * 运行命令，需要确保PATH可用
	 */
	protected void runCommands() {
		// 根据pom.xml文件现在工具包
		File pomFile = new File(MyConfiguration.getWorkFolder(), "classes" + File.separator + "pom.btulz.xml");
		if (!pomFile.isFile() || !pomFile.exists()) {
			return;
		}
		try {
			int exitCode = 0;
			/** 初始化工具包 */
			// mvn -f .../pom.xml dependency:copy-dependencies
			exitCode = this.runCommand(
					Strings.concat("mvn", " -f", Strings.format(" \"%s\"", pomFile.getPath()),
							" dependency:copy-dependencies"),
					MyConfiguration.getTempFolder(), new KeyText("TMPDIR", MyConfiguration.getTempFolder()));
			if (exitCode != 0) {
				return;
			} else {
				Logger.log("command: get jars to [%s]. ",
						MyConfiguration.getTempFolder() + File.separator + "ibas_tools");
			}
			File tsFile = new File(MyConfiguration.getTempFolder(),
					"ibas_tools" + File.separator + "btulz.transforms.bobas-0.2.0.jar");
			if (!tsFile.exists() || !tsFile.isFile()) {
				return;
			}
			File cgFile = new File(MyConfiguration.getWorkFolder(), "app.xml");
			if (!cgFile.exists() || !cgFile.isFile()) {
				return;
			}
			/** 初始化数据库 */
			// java -jar btulz.transforms.core.jar ds
			// -data=.../ds_tt_trainingtesting.xml
			// -config=.../app.xml
			// 基础结构
			exitCode = this
					.runCommand(
							Strings.concat("java", Strings.format(" -cp \"%s\"", tsFile.getParent()),
									Strings.format(" -jar \"%s\" ds", tsFile.getPath()),
									Strings.format(" \"-data=%s\"",
											new File(org.colorcoding.ibas.bobas.MyConfiguration.getWorkFolder(),
													"lib/bobas.businessobjectscommon.jar")),
									Strings.format(" \"-config=%s\"", cgFile.getPath())),
							MyConfiguration.getDataFolder());
			if (exitCode != 0) {
				return;
			}
			// 数据库特性
			exitCode = this
					.runCommand(
							Strings.concat("java", Strings.format(" -cp \"%s\"", tsFile.getParent()),
									Strings.format(" -jar \"%s\" ds", tsFile.getPath()),
									Strings.format(" \"-data=%s\"",
											new File(org.colorcoding.ibas.bobas.MyConfiguration.getWorkFolder(),
													"lib/bobas.businessobjectscommon.db.sqlite.jar")),
									Strings.format(" \"-config=%s\"", cgFile.getPath())),
							MyConfiguration.getDataFolder());
			if (exitCode != 0) {
				return;
			}
			// 功能结构
			File dsFile = new File(pomFile.getParent(),
					"datastructures" + File.separator + "ds_tt_trainingtesting.xml");
			if (!dsFile.exists() || !dsFile.isFile()) {
				return;
			}
			exitCode = this.runCommand(Strings.concat("java", Strings.format(" -cp \"%s\"", tsFile.getParent()),
					Strings.format(" -jar \"%s\" ds", tsFile.getPath()),
					Strings.format(" \"-data=%s\"", dsFile.getPath()),
					Strings.format(" \"-config=%s\"", cgFile.getPath())), MyConfiguration.getDataFolder());
			if (exitCode != 0) {
				return;
			}
			// 初始数据，需要先编译demo项目（使用其jar包）
			pomFile = new File(MyConfiguration.getWorkFolder(), "classes" + File.separator + "pom.demo.xml");
			if (!pomFile.isFile() || !pomFile.exists()) {
				return;
			}
			exitCode = this.runCommand(
					Strings.concat("mvn", " -f", Strings.format(" \"%s\"", pomFile.getPath()),
							" dependency:copy-dependencies"),
					MyConfiguration.getTempFolder(), new KeyText("TMPDIR", MyConfiguration.getTempFolder()));
			if (exitCode != 0) {
				return;
			} else {
				Logger.log("command: get demo jar to [%s]. ",
						MyConfiguration.getTempFolder() + File.separator + "ibas_demo");
			}
			dsFile = new File(MyConfiguration.getTempFolder(),
					"ibas_demo" + File.separator + "bobas.businessobjectscommon.demo-0.2.0.jar");
			if (!dsFile.exists() || !dsFile.isFile()) {
				return;
			}
			exitCode = this.runCommand(
					Strings.concat("java", Strings.format(" -cp \"%s\"", tsFile.getParent()),
							Strings.format(" -jar \"%s\" init", tsFile.getPath()),
							Strings.format(" \"-data=%s\"", dsFile.getPath()),
							Strings.format(" \"-config=%s\"", cgFile.getPath()),
							Strings.format(" \"-classes=%s\"", dsFile.getParent() + File.separator)),
					MyConfiguration.getWorkFolder(), new KeyText("user.dir", MyConfiguration.getWorkFolder()));
			if (exitCode != 0) {
				return;
			}
		} catch (IOException | InterruptedException e) {
			Logger.log(e);
		}

	}

	protected int runCommand(String command, String workFolder, KeyText... environments)
			throws IOException, InterruptedException {
		int exitCode;
		String line;
		Process process;
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.redirectErrorStream(true);
		// 继承当前变量
		processBuilder.inheritIO();
		// 设置工作目录
		if (!Strings.isNullOrEmpty(workFolder)) {
			processBuilder.directory(new File(workFolder));
			if (!processBuilder.directory().exists()) {
				processBuilder.directory().mkdirs();
			}
		}
		// 设置环境变量
		if (environments != null && environments.length > 0) {
			for (KeyText item : environments) {
				processBuilder.environment().put(item.getKey(), item.getText());
			}
		}
		// 设置PATH
		if (Strings.indexOf(System.getProperty("os.name"), "Mac") >= 0) {
			processBuilder.environment().put("PATH", Strings.concat(System.getenv("PATH"), ":/usr/local/bin"));
		} else {
			processBuilder.environment().put("PATH", System.getenv("PATH"));
		}
		// 构建命令
		processBuilder = processBuilder.command("/bin/sh", "-c", command);
		// 启动进程
		System.out.println(Strings.format("command: start, %s", command));
		process = processBuilder.start();
		// 读取输出
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}
		// 等待命令完成
		exitCode = process.waitFor();
		if (exitCode != 0) {
			System.err.println(Strings.format("command: faild, %s", command));
		} else {
			System.out.println(Strings.format("command: done, %s", command));
		}
		// 释放资源
		process.destroy();
		process = null;
		return exitCode;
	}
}