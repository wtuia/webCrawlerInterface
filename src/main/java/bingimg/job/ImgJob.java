package bingimg.job;

import bingimg.bean.BingImg;
import bingimg.db.DB;
import common.PropertiesHandle;
import message.SendMessage;
import org.apache.logging.log4j.core.config.Configurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(ImgJob.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String PARENT_PATH = PropertiesHandle.getResourceInfoSetDefault("bingImg.savePath",
			"/data/bing/");
	private static final String NET_PATH = PropertiesHandle.getResourceInfoSetDefault("bingImg.netPath",
			"/usr/local/apache-tomcat-8.5.43/webapps/bingImgs/");
	private static final String NET_ADDR = "http://139.155.83.148/bingImgs/";
	private static Pattern pattern = Pattern.compile("//+th\\?id=OHR\\.([-._A-Za-z0-9]+)");
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
	private String time;
	private int year, month;
	public static void main(String[] args) {
		Configurator.initialize("log4j2.xml",
				System.getProperty("user.dir") + File.separator + "log4j2.xml");
		new ImgJob().execute(null);
	}
	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		time = SDF.format(System.currentTimeMillis());
		BingImg bingImg;
		int reCrawler = 0;
		do {
			bingImg = crawlerImg();
			reCrawler ++;
		}while (bingImg == null && reCrawler <= 3); // 重试3次
		if (reCrawler > 3) {
			bingImg = createWarnData();
		}else {
			DB.save(bingImg);
		}
		send(bingImg);
	}

	public BingImg createWarnData() {
		BingImg img = new BingImg();
		img.setTitle("");
		img.setTitle("");
		img.setContent("获取文件异常，错误信息请查看日志");
		return img;
	}

	private BingImg crawlerImg() {
		try {
			LocalDateTime localDateTime = LocalDateTime.now();
			Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
			Document document = Jsoup.connect(BingImg.getBaseUrl()).get();
			String imgHref = document.getElementsByTag("link").eq(0).first().attr("href");
			Element description = document.getElementsByClass("sc_light").first();
			String title = description.attr("title");
			String titleHref = description.attr("href");
			BingImg bingImg = new BingImg(imgHref, title, titleHref, date);
			URL url = new URL(bingImg.getImageUrl());
			setFilePathANdName(url, localDateTime, bingImg);
			FileUtil.checkFileAndSave(url, bingImg);
			bingImg.setContent(getContent(bingImg));
			return bingImg;
		}catch (Exception e){
			logger.error("获取数据失败，重新获取", e);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				logger.error("",e);
			}
			return null;
		}
	}

	private String getContent(BingImg img) {
		String netPath = NET_ADDR + year + File.separator + month + File.separator + img.getFileName();
		String title = img.getTitle().replace("© ", "");
		return "<a href=\"" + img.getTitleUrl() + "\">" + title + "</a>" +
				"<br>(内容来源于<a href=\"https://cn.bing.com/\">必应</a>)<br>"+
				"<a href=\""+ netPath +"\"><img src=\""+ netPath +"\" style=\"width:200px\"/></a><span>点击获取原图</span>";
	}

	private void send(BingImg bingImg) {
		String subject = "("+time+")"+bingImg.getTitle().substring(0, bingImg.getTitle().indexOf("("));
		String content = bingImg.getContent();
		String[] to = new String[]{"1424471149@qq.com","1195474371@qq.com"};
		SendMessage.send(to,subject, content, null);
	}

	private void setFilePathANdName(URL url, LocalDateTime localDateTime, BingImg bingImg) {
		Matcher matcher = pattern.matcher(url.getFile());
		String fileName = localDateTime.format(formatter) + "_";
		if (matcher.find()) {
			fileName += matcher.group(1);
		}else {
			fileName += UUID.randomUUID() + ".jpg";
		}
		bingImg.setFileName(fileName);
		year = localDateTime.getYear();
		month = localDateTime.getMonthValue();
		String savePath = PARENT_PATH + year + File.separator + month;
		bingImg.setFilePath(savePath);
		bingImg.setNetFilePath(NET_PATH + year + File.separator + month);
	}

	public static String triggerExpression() {
		String startTime = PropertiesHandle.
				getResourceInfoSetDefault("bingImgTask.startTime", "00:30");
		String[] startTimes = startTime.split(":");
		startTime = "0 " + startTimes[1] + " " + startTimes[0] + " * * ?";
		return startTime;
	}

}
