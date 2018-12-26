package com.shizy.apkreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;

public class ApkReader {

	private static final String PATH_AAPT = "D:\\Android\\Sdk\\build-tools\\28.0.2\\aapt.exe";

	public static void main(String[] args) throws Exception {
		System.out.println(args);
		if (args.length < 2) {
			System.out.println("Params[0]: aapt");
			System.out.println("Params[1]: apkPath");
			return;
		}

		dumpApk(args[0], args[1]);
	}

	public static void dumpApk(String aapt, String apkPath) {
		File apkFile = new File(apkPath);
		if (!apkFile.exists()) {
			System.out.println(apkPath + " not exists!");
			return;
		}

		ProcessBuilder command = new ProcessBuilder(aapt, "d", "badging", apkPath);
		Process process = null;
		InputStream is = null;
		try {
			process = command.start();
			is = process.getInputStream();
			ApkInfo info = DumpParser.parse(is);
			is.close();
			is = null;

			File parentFile = apkFile.getParentFile();

			String jsonPath = generateFilePath(parentFile.getAbsolutePath(), info.getPackageName(), ".json");
			saveInfoToFile(info, jsonPath);

			String iconPath = generateFilePath(parentFile.getAbsolutePath(), info.getPackageName(),
					info.getIcon().substring(info.getIcon().lastIndexOf(".")));
			boolean success = ZipUtil.extractFileFromZip(apkPath, info.getIcon(), iconPath);
			System.out.println("Extract icon success: " + success);

			renameApkFile(info, apkPath);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
			if (process != null) {
				process.destroy();
			}
		}
	}

	private static void saveInfoToFile(ApkInfo info, String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}

		FileWriter writer = null;
		try {
			file.createNewFile();
			writer = new FileWriter(file);
			writer.write(new Gson().toJson(info));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer = null;
			}
		}

	}

	private static String generateFilePath(String parentPath, String name, String ext) {
		StringBuilder builder = new StringBuilder();
		builder.append(parentPath);
		builder.append(File.separator);
		builder.append(name);
		builder.append(ext);
		return builder.toString();
	}

	private static void renameApkFile(ApkInfo info, String apkPath) {
		File file = new File(apkPath);
		File dest = new File(file.getParentFile(), info.getPackageName() + ".apk");
		System.out.println(dest.getAbsolutePath());
		if (dest.exists()) {
			dest.delete();
		}
		file.renameTo(dest);
	}

}
