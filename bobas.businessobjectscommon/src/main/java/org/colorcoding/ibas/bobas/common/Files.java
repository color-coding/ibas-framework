package org.colorcoding.ibas.bobas.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class Files {

	private Files() {
	}

	public static File valueOf(String first, String... more) {
		return Paths.get(first, more).normalize().toFile();
	}

	public static void writeTo(File file, OutputStream outputStream) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
	}
}
