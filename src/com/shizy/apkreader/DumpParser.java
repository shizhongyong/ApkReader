package com.shizy.apkreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DumpParser {

	private static final String PACKAGE = "package:";
	private static final String SDK_VERSION = "sdkVersion:";
	private static final String TARGET_SDK_VERSION = "targetSdkVersion:";
	private static final String USES_PERMISSION = "uses-permission:";
	private static final String APPLICATION = "application:";

	private static final String NAME = "name";
	private static final String VERSION_CODE = "versionCode";
	private static final String VERSION_NAME = "versionName";
	private static final String LABEL = "label";
	private static final String ICON = "icon";

	public static ApkInfo parse(InputStream is) {
		if (is == null) {
			return null;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line = br.readLine();
			if (line == null || !line.startsWith(PACKAGE)) {
				return null;
			}

			ApkInfo info = new ApkInfo();

			do {
				parseLine(info, line);
				line = br.readLine();
			} while (line != null);

			return info;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}

		return null;
	}

	private static void parseLine(ApkInfo info, String line) {
		if (line.startsWith(PACKAGE)) {
			parsePackage(info, line.substring(PACKAGE.length()));
		} else if (line.startsWith(SDK_VERSION)) {
			parseSdkVersion(info, line.substring(SDK_VERSION.length()));
		} else if (line.startsWith(TARGET_SDK_VERSION)) {
			parseTargetSdkVersion(info, line.substring(TARGET_SDK_VERSION.length()));
		} else if (line.startsWith(USES_PERMISSION)) {
			parsePermissions(info, line.substring(USES_PERMISSION.length()));
		} else if (line.startsWith(APPLICATION)) {
			parseApplication(info, line.substring(APPLICATION.length()));
		}
	}

	private static void parsePackage(ApkInfo info, String line) {
		Map<String, String> properties = parseProperties(line);

		info.setPackageName(properties.get(NAME));
		info.setVersionCode(properties.get(VERSION_CODE));
		info.setVersionName(properties.get(VERSION_NAME));
	}

	private static void parseSdkVersion(ApkInfo info, String line) {
		info.setMinSdkVersion(line.substring(1, line.length() - 1));
	}

	private static void parseTargetSdkVersion(ApkInfo info, String line) {
		info.setTargetSdkVersion(line.substring(1, line.length() - 1));
	}

	private static void parsePermissions(ApkInfo info, String line) {
		Map<String, String> properties = parseProperties(line);

		List<String> permissions = info.getPermissions();
		if (permissions == null) {
			permissions = new ArrayList<>();
			info.setPermissions(permissions);
		}
		permissions.add(properties.get(NAME));
	}

	private static void parseApplication(ApkInfo info, String line) {
		Map<String, String> properties = parseProperties(line);

		info.setName(properties.get(LABEL));
		info.setIcon(properties.get(ICON));
	}

	private static Map<String, String> parseProperties(String line) {
		HashMap<String, String> properties = new HashMap<>();
		String[] pairs = line.trim().split(" +");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			if (keyValue == null || keyValue.length != 2) {
				continue;
			}
			properties.put(keyValue[0], keyValue[1].substring(1, keyValue[1].length() - 1));
		}
		return properties;
	}

}
