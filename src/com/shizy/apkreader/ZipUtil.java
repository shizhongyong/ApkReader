package com.shizy.apkreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

	public static boolean extractFileFromZip(String zipFile, String extractFile, String outFile) {
		ZipFile zip = null;
		InputStream is = null;

		try {
			zip = new ZipFile(zipFile);
			ZipEntry entry = zip.getEntry(extractFile);
			is = zip.getInputStream(entry);

			if (is == null) {
				return false;
			}

			File file = new File(outFile);
			if (file.exists() && !file.delete()) {
				return false;
			}

			File parentFile = file.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdir()) {
				return false;
			}

			if (!file.createNewFile()) {
				return false;
			}

			FileOutputStream fos = new FileOutputStream(file);
			byte[] buff = new byte[1024];
			int len = -1;
			while ((len = is.read(buff)) > 0) {
				fos.write(buff, 0, len);
			}
			fos.flush();
			fos.close();
			fos = null;

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zip != null) {
					zip.close();
					zip = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
