package org.colorcoding.ibas.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.message.Logger;
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
		File pomFile = Files.valueOf(MyConfiguration.getWorkFolder(), "classes", "pom.btulz.xml");
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
			File tsFile = Files.valueOf(MyConfiguration.getTempFolder(), "ibas_tools",
					"btulz.transforms.bobas-0.2.0.jar");
			if (!tsFile.exists() || !tsFile.isFile()) {
				return;
			}
			File cgFile = new File(MyConfiguration.getWorkFolder(), "app.xml");
			if (!cgFile.exists() || !cgFile.isFile()) {
				return;
			}
			// 复制app.xml到ibas_tools目录，并修改MasterDbName为完整路径
			// 原因：btulz子进程工作目录可能不同，sqlite的DbUrl=jdbc:sqlite:${DbName}使用相对路径，
			// 导致数据库文件创建/查找位置不一致
			File toolsCgFile = new File(tsFile.getParent(), "app.xml");
			this.patchAppXml(cgFile, toolsCgFile,
					MyConfiguration.getDataFolder());
			/** 初始化数据库 */
			// java -cp ".../*" org.colorcoding.tools.btulz.bobas.Console ds
			// -data=.../ds_tt_trainingtesting.xml
			// -config=.../app.xml
			// 注意：使用-jar时Java会忽略-cp参数，仅使用MANIFEST.MF中的Class-Path，
			// 导致sqlite-jdbc等未在MANIFEST.MF中声明的依赖无法加载，
			// 因此改用-cp通配符 + 指定主类的方式运行
			// 基础结构
			exitCode = this.runCommand(Strings.concat("java", Strings.format(" -cp \"%s/*\"", tsFile.getParent()),
					" org.colorcoding.tools.btulz.bobas.Console ds",
					Strings.format(" \"-data=%s\"",
							new File(org.colorcoding.ibas.bobas.MyConfiguration.getWorkFolder(),
									"lib/bobas.businessobjectscommon.jar")),
					Strings.format(" \"-config=%s\"", toolsCgFile.getPath())), MyConfiguration.getDataFolder());
			if (exitCode != 0) {
				return;
			}
			// 数据库特性
			exitCode = this.runCommand(Strings.concat("java", Strings.format(" -cp \"%s/*\"", tsFile.getParent()),
					" org.colorcoding.tools.btulz.bobas.Console ds",
					Strings.format(" \"-data=%s\"",
							new File(org.colorcoding.ibas.bobas.MyConfiguration.getWorkFolder(),
									"lib/bobas.businessobjectscommon.db.sqlite.jar")),
					Strings.format(" \"-config=%s\"", toolsCgFile.getPath())), MyConfiguration.getDataFolder());
			if (exitCode != 0) {
				return;
			}
			// 功能结构
			File dsFile = Files.valueOf(pomFile.getParent(), "datastructures", "ds_tt_trainingtesting.xml");
			if (!dsFile.exists() || !dsFile.isFile()) {
				return;
			}
			exitCode = this.runCommand(Strings.concat("java", Strings.format(" -cp \"%s/*\"", tsFile.getParent()),
					" org.colorcoding.tools.btulz.bobas.Console ds", Strings.format(" \"-data=%s\"", dsFile.getPath()),
					Strings.format(" \"-config=%s\"", toolsCgFile.getPath())), MyConfiguration.getDataFolder());
			if (exitCode != 0) {
				return;
			}
			// 初始数据，需要先编译demo项目（使用其jar包）
			pomFile = Files.valueOf(MyConfiguration.getWorkFolder(), "classes", "pom.demo.xml");
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
			dsFile = Files.valueOf(MyConfiguration.getTempFolder(), "ibas_demo",
					"bobas.businessobjectscommon.demo-0.2.0.jar");
			if (!dsFile.exists() || !dsFile.isFile()) {
				return;
			}
			// 工作目录使用dataFolder，与ds命令一致，确保app.xml中相对路径能找到ds命令创建的库文件
			exitCode = this.runCommand(
					Strings.concat("java", Strings.format(" -cp \"%s/*\"", tsFile.getParent()),
							" org.colorcoding.tools.btulz.bobas.Console init",
							Strings.format(" \"-data=%s\"", dsFile.getPath()),
							Strings.format(" \"-config=%s\"", toolsCgFile.getPath()),
							Strings.format(" \"-classes=%s\"", dsFile.getParent() + File.separator)),
					MyConfiguration.getDataFolder(), new KeyText("user.dir", MyConfiguration.getWorkFolder()));
			if (exitCode != 0) {
				return;
			}
		} catch (IOException | InterruptedException e) {
			Logger.log(e);
		}

	}

	/**
	 * 复制app.xml到目标位置，并将MasterDbName的值补全为绝对路径。
	 * 原因：btulz模板中sqlite的DbUrl为jdbc:sqlite:${DbName}，
	 * 若MasterDbName仅为文件名（如ibas_demo_v2），sqlite会相对于子进程工作目录创建/查找库文件，
	 * 导致各子命令之间库文件路径不一致。补全路径后，无论子进程工作目录在哪都能定位到同一库文件。
	 *
	 * @param srcFile  原始app.xml
	 * @param destFile 目标app.xml
	 * @param dataFolder 数据目录，MasterDbName值将补全为此目录下的绝对路径
	 */
	protected void patchAppXml(File srcFile, File destFile, String dataFolder) {
		try {
			String xml = new String(java.nio.file.Files.readAllBytes(srcFile.toPath()), "UTF-8");
			// 匹配MasterDbName的值，如 key="MasterDbName" value="ibas_demo_v2"
			// 将value补全为绝对路径：dataFolder/原值
			// 使用Pattern+Matcher方式，兼容Java 8
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
					"(key=\"MasterDbName\"\\s+value=\")([^\"\\\\]+)(\")");
			java.util.regex.Matcher matcher = pattern.matcher(xml);
			StringBuffer sb = new StringBuffer();
			boolean patched = false;
			while (matcher.find()) {
				String original = matcher.group(2);
				// 如果已经是绝对路径则不再补全
				if (new File(original).isAbsolute()) {
					matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(matcher.group(0)));
				} else {
					matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(
							matcher.group(1) + new File(dataFolder, original).getPath() + matcher.group(3)));
					patched = true;
				}
			}
			matcher.appendTail(sb);
			if (patched) {
				java.nio.file.Files.write(destFile.toPath(), sb.toString().getBytes("UTF-8"));
			} else {
				// 没有修改，直接复制
				java.nio.file.Files.copy(srcFile.toPath(), destFile.toPath(),
						java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			}
			Logger.log("patch app.xml: copy [%s] to [%s], MasterDbName patched with [%s].",
					srcFile.getPath(), destFile.getPath(), dataFolder);
		} catch (IOException e) {
			Logger.log(e);
		}
	}

	protected int runCommand(String command, String workFolder, KeyText... environments)
			throws IOException, InterruptedException {
		int exitCode;
		String line;
		Process process;
		ProcessBuilder processBuilder = new ProcessBuilder();
		// 合并错误流到输出流，方便统一读取
		processBuilder.redirectErrorStream(true);
		// 设置工作目录（仅影响子进程，不改变当前JVM的工作目录）
		if (!Strings.isNullOrEmpty(workFolder)) {
			File workDir = new File(workFolder);
			if (!workDir.exists()) {
				workDir.mkdirs();
			}
			processBuilder.directory(workDir);
		}
		// 设置环境变量（仅影响子进程，不改变当前JVM的环境）
		java.util.Map<String, String> env = processBuilder.environment();
		if (environments != null && environments.length > 0) {
			for (KeyText item : environments) {
				env.put(item.getKey(), item.getText());
			}
		}
		// 设置PATH
		if (Strings.indexOf(System.getProperty("os.name"), "Mac") >= 0) {
			env.put("PATH", Strings.concat(System.getenv("PATH"), ":/usr/local/bin"));
		} else {
			env.put("PATH", System.getenv("PATH"));
		}
		// 构建命令
		processBuilder.command("/bin/sh", "-c", command);
		// 启动进程
		System.out.println(Strings.format("command: start, %s", command));
		process = processBuilder.start();
		// 读取输出（不使用inheritIO，手动读取以避免输出重复或丢失）
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