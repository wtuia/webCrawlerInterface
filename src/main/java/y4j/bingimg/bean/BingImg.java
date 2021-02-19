package y4j.bingimg.bean;

import y4j.bingimg.job.TypeEnum;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class BingImg {
	private static final String baseUrl = "http://cn.bing.com/";
	private final Map<String, String> imageUrl = new HashMap<>();
	private String title;
	private String titleUrl;
	private String filePath;
	private String fileName;
	private Date recordTime;
	private String fullName;
	private String content;

	public BingImg() {
	}

	public BingImg(String url, String title, String titleUrl, java.util.Date recordTime) {
		url = url.substring(0, url.indexOf("jpg") + 3);
		for (TypeEnum typeEnum : y4j.bingimg.job.TypeEnum.values()) {
			imageUrl.put(typeEnum.getType(), baseUrl + url.replace("1920x1080", typeEnum.getSize()));
		}
		this.title = title;
		this.titleUrl = titleUrl;
		this.recordTime = new Date(recordTime.getTime());
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public static String getBaseUrl() {
		return baseUrl;
	}
	
	public Map<String, String> getImageUrl() {
		return imageUrl;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getTitleUrl() {
		return baseUrl + titleUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	@Override
	public String toString() {
		return "BingImg{" +
				"imageUrl='" + imageUrl + '\'' +
				", title='" + title + '\'' +
				", titleUrl='" + titleUrl + '\'' +
				", recordTime=" + recordTime +
				", fullName='" + fullName + '\'' +
				'}';
	}
}
