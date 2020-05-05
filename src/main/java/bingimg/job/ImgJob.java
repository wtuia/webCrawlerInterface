package bingimg.job;

import bingimg.bean.BingImg;
import com.zone.util.SendMessage;
import common.PropertiesHandle;
import bingimg.db.DB;
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
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String PARENT_PATH =
			PropertiesHandle.getResourceInfoSetDefault("bingImg.savePath", "/data/bing/");
	private static Pattern pattern = Pattern.compile("//+th\\?id=OHR\\.([-._A-Za-z0-9]+)");
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

	public static void main(String[] args) {
		Configurator.initialize("log4j2.xml",
				System.getProperty("user.dir") + File.separator + "log4j2.xml");
		new ImgJob().execute(null);
	}
	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
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
			DB.save(bingImg);
			String time = SDF.format(System.currentTimeMillis());
			String subject = "执行结果:" + time;
			String content = "必应每日一图在附件内:";
			String[] files = new String[]{bingImg.getFullName()};
			String[] to = new String[]{"1424471149@qq.com","1195474371@qq.com"};
			SendMessage.send(to,subject, content, files);
		} catch (Exception e) {
			logger.error("任务执行异常", e);
		}
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
		int year = localDateTime.getYear();
		int month = localDateTime.getMonthValue();
		String savePath = PARENT_PATH + year + File.separator + month;
		bingImg.setFilePath(savePath);
	}

	public static String triggerExpression() {
		String startTime = PropertiesHandle.
				getResourceInfoSetDefault("bingImgTask.startTime", "00:30");
		String[] startTimes = startTime.split(":");
		startTime = "0 " + startTimes[1] + " " + startTimes[0] + " * * ?";
		return startTime;
	}

}
