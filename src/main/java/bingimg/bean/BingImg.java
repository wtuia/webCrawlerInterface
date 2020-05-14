package bingimg.bean;

import java.util.Date;

public class BingImg {
	private static final String baseUrl = "https://cn.bing.com/";
	private String imageUrl;
	private String title;
	private String titleUrl;
	private String filePath;
	private String fileName;
	private Date recordTime;
	private String fullName;
	private String content;

	public BingImg() {
	}

	public BingImg(String url, String title, String titleUrl, Date recordTime) {
		this.imageUrl = url;
		this.title = title;
		this.titleUrl = titleUrl;
		this.recordTime = recordTime;
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

	public String getImageUrl() {
		return baseUrl + imageUrl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
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
