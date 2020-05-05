import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FTPUtil {
	private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

	private static String server;
	private static String user;
	private static String pass;

	static {
		try {
			server = "139.155.83.148";
			user = "yj";
			pass = "yj357322";
		}catch (Exception e) {
			logger.error("加载FTP配置异常", e);
		}
	}


	public static void main(String[] args) {
		FTPClient ftpClient = FTPUtil.getFTPClient();
		try {
			String path = ftpClient.printWorkingDirectory();
			System.out.println(path);
			/*for (FTPFile ftpFile1 : ftpFile) {
				System.out.println(ftpFile1.getName());
			}*/
			//upload(ftpClient, "C:\\Users\\14244\\Desktop\\out.docx", "tst.out");
			ftpClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 建立连接
	 */
	public static FTPClient getFTPClient() {
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(server, 21);
			ftpClient.login(user, pass);
			int status = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(status)) {
				logger.error("{} ftp连接失败, status={}", server, status);
			}
		}catch (Exception e) {
			logger.error("{} ftp连接失败" ,server, e);
		}
		return ftpClient;
	}

	/**
	 * 断开连接
	 */
	public static void disConnect(FTPClient ftpClient) {
		if (ftpClient == null) {
			logger.error("[{}]登出失败，ftp未建立连接", server);
			return;
		}
		try {
			ftpClient.logout();
		} catch (IOException e) {
			logger.error("[{}]登出ftp失败", server, e);
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					logger.error("[{}]断开ftp连接失败",server, e);
				}
			}
		}
	}

	public static void upload(String path, String pathAndName, String localName) {
		FTPClient ftpClient = getFTPClient();
		//upload(ftpClient, path, pathAndName, localName);
		disConnect(ftpClient);
	}

	public static void upload(FTPClient ftpClient, String localName, String fileName) {
		InputStream in = null;
		try {
			ftpClient.changeWorkingDirectory("localFtpFile");
			ftpClient.enterLocalPassiveMode();
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			in = new FileInputStream(localName);
			if (!ftpClient.storeFile(new String(fileName.getBytes("UTF-8"),
					StandardCharsets.ISO_8859_1), in)) {
				logger.error("上传文件失败{}", localName);
			}
		} catch (Exception e) {
			logger.error("上传文件{}失败", localName, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("inputStream关闭异常", e);
				}
			}
		}
	}

	/**
	 *	判断文件是否存在
	 */
	private static void delExistsFile(FTPClient ftpClient, String pathAndName){
		try {
			FTPFile[] files = ftpClient.listFiles(pathAndName);
			if (files != null && files.length > 0) {
				logger.info("文件存在，清空之前文件");
				ftpClient.deleteFile(pathAndName);
			}
		} catch (Exception e) {
			logger.error("获取文件{}状态异常", pathAndName);
		}
	}

	public static String getServer() {
		return server;
	}
}
