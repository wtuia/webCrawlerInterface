package common;


import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class PropertiesHandle {

	private static final Logger logger = Logger.getLogger(PropertiesHandle.class);

	private static PropertiesHandle PROP_HANDLE;
	private static String DEFAULT_FILENAME = "y4j.properties";
	private Properties props;

	private PropertiesHandle(String filename) {
		this.load(filename);

	}

	public static String getResourceInfoSetDefault(String key, String defaultValue) {
		if ( PROP_HANDLE == null) {
			PROP_HANDLE = new PropertiesHandle(DEFAULT_FILENAME);
		}
		String prop = PROP_HANDLE.getProperties(key);
		if (prop == null) {
			return defaultValue;
		}
		return prop;
	}

	static PropertiesHandle getPropertiesHandle(String filename) {
		if (DEFAULT_FILENAME.equalsIgnoreCase(filename)) {
			return PROP_HANDLE == null ? new PropertiesHandle(filename) : PROP_HANDLE;
		}else {
			return new PropertiesHandle(filename);
		}
	}

	private String getProperties(String key) {
		return props.getProperty(key);
	}

	private void load(String filename) {
		props = new Properties();
		String currentDir = System.getProperty("user.dir");
		filename = currentDir + File.separator  + filename;
		try {
			InputStream in = new FileInputStream(filename);
			props.load(in);
			in.close();
		} catch (IOException e) {
			logger.error("初始化文件失败", e);
		}
	}
}
