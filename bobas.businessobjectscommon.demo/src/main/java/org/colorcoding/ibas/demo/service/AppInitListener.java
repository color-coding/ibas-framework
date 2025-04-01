package org.colorcoding.ibas.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.demo.MyConfiguration;

@WebListener
public class AppInitListener implements ServletContextListener {

	static String COMMAND_GET_JARS =
			// "mvn -f \"%s\" dependency:get -Ddest=\"%s\" ";
			"mvn dependency:get -Dartifact=org.colorcoding.tools:btulz.transforms.core:0.1.1 -DremoteRepositories=http://maven.colorcoding.org/repository/maven-public/ -Ddest=$pwd";
	static String COMMAND_INIT_DB = "java -jar %s ds -data=\"%s\" -config=\"%s\"";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		File pomFile = new File(MyConfiguration.getWorkFolder(), "classes" + File.separator + "pom.btulz.xml");
		if (!pomFile.isFile() || !pomFile.exists()) {
			return;
		}
		File workFolder = new File(MyConfiguration.getTempFolder());
		if (!workFolder.exists()) {
			workFolder.mkdirs();
		}
		int exitCode;
		Process process;
		ProcessBuilder processBuilder = new ProcessBuilder();
		// 合并错误流到标准输出（简化处理）
		processBuilder.redirectErrorStream(true);
		// 设置工作目录
		processBuilder.directory(workFolder);
		// 设置环境变量
		processBuilder.environment().put("PATH", System.getenv("PATH"));
		try {
			processBuilder = processBuilder
					.command(Strings.format(COMMAND_GET_JARS, pomFile.getPath(), workFolder.getPath()));
			// 启动进程
			process = processBuilder.start();
			// 读取输出
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					Logger.log(line);
				}
			}
			// 等待命令完成
			exitCode = process.waitFor();
			Logger.log("command_get_jars done, code: %s. ", exitCode);
			if (exitCode != 0) {
				return;
			}
			File tsFile = new File(MyConfiguration.getTempFolder(), "btulz.transforms.bobas-0.1.0.jar");
			if (!tsFile.exists() || !tsFile.isFile()) {
				return;
			}
			File dsFile = new File(MyConfiguration.getWorkFolder(),
					"classes" + File.separator + "datastructures" + File.separator + "ds_tt_trainingtesting.xml");
			if (!dsFile.exists() || !dsFile.isFile()) {
				return;
			}
			File cgFile = new File(MyConfiguration.getWorkFolder(), "app.xml");
			if (!cgFile.exists() || !cgFile.isFile()) {
				return;
			}
			processBuilder = processBuilder
					.command(Strings.format(COMMAND_INIT_DB, tsFile.getPath(), dsFile.getPath(), cgFile.getPath()));
			// 启动进程
			process = processBuilder.start();
			// 读取输出
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					Logger.log(line);
				}
			}
			// 等待命令完成
			exitCode = process.waitFor();
			Logger.log("command_init_db done, code: %s. ", exitCode);
			if (exitCode != 0) {
				return;
			}
		} catch (IOException | InterruptedException e) {
			Logger.log(e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}