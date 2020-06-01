package util;

import bingimg.bean.BingImg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;


public class FileUtil {
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static void checkFileAndSave(URL url, BingImg bingImg) {
		File pathFile = new File(bingImg.getFilePath());
		if (!pathFile.exists()) {
			if (!pathFile.mkdirs()){
				logger.error("创建文件夹{}失败", bingImg.getFilePath());
			}
		}
		File netPath = new File(bingImg.getNetFilePath());
		if (!netPath.exists()) {
			if (!netPath.mkdirs()){
				logger.error("创建文件夹{}失败", bingImg.getFilePath());
			}
		}
		String pathAndFileName = bingImg.getFilePath() + File.separator + bingImg.getFileName();
		bingImg.setFullName(pathAndFileName);
		saveFile(pathAndFileName, url);
		String netPathAndFileName = bingImg.getNetFilePath() + File.separator + bingImg.getFileName();
		saveFile(netPathAndFileName, url);
	}

	private static void saveFile(String pathAndName, URL url) {
		logger.info("创建文件:{}", pathAndName);
		OutputStream os = null;
		InputStream in = null;
		try {
			in = url.openStream();
			int len;
			os = new FileOutputStream(new File(pathAndName));
			byte[] buffer = new byte[2048];
			int i =0;
			while ((len = in.read(buffer)) != -1) {
				i++;
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (IOException e) {
			logger.error("保存文件异常", e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.info("关闭输入流异常", e);
			}
		}
	}
}
