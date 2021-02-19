package y4j.bingimg.job;

import org.springframework.beans.factory.annotation.Autowired;
import y4j.bingimg.bean.BingImg;
import y4j.bingimg.db.ImgDB;
import org.apache.logging.log4j.core.config.Configurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

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

public class ImgJob extends QuartzJobBean {
	private static final Logger logger = LoggerFactory.getLogger(ImgJob.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final Pattern pattern = Pattern.compile("//+th\\?id=OHR\\.([-._A-Za-z0-9]+)");
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
	
	@Autowired
	private ImgDB imgDB;
	@Value("${bingImg.savePath}")
	private String PARENT_PATH;
	private String time;
	
	public static void main(String[] args) {
		Configurator.initialize("log4j2.xml",
				System.getProperty("user.dir") + File.separator + "log4j2.xml");
		new ImgJob().executeInternal(null);
	}
	@Override
	public void executeInternal(JobExecutionContext jobExecutionContext) {
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
			imgDB.save(bingImg);
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
			URL url = new URL(bingImg.getImageUrl().get(TypeEnum.HD.getType()));
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
		String title = img.getTitle().replace("© ", "");
		StringBuilder builder = new StringBuilder("<p><a href=\"" + img.getTitleUrl() + "\">" + title + "</a>" +
				"(内容来源于<a href=\"https://cn.bing.com/\">必应</a>)</p>");
		builder.append("<p><img src=\"").append(img.getImageUrl().get(TypeEnum.HD.getType()))
				.append("\" style=\"width:200px\"/><span>预览图</span></p>");
		for (TypeEnum typeEnum : TypeEnum.values()) {
			builder.append("<p><a href=\"").append(img.getImageUrl().get(typeEnum.getType())).append("\">")
					.append(typeEnum.getType()).append("(").append(typeEnum.getSize()).append(")").append("</a></p>");
		}
		return builder.toString();
	}

	private void send(BingImg bingImg) {
		String subject;
		if(bingImg.getTitle().contains("("))
			subject = "("+time+")"+bingImg.getTitle().substring(0, bingImg.getTitle().indexOf("("));
		else
			subject = "("+time+")"+bingImg.getTitle();
		String content = bingImg.getContent();
		String[] to = new String[]{"1424471149@qq.com"};
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
		int year = localDateTime.getYear();
		int month = localDateTime.getMonthValue();
		String savePath = PARENT_PATH + year + File.separator + month;
		bingImg.setFilePath(savePath);
	}

}
