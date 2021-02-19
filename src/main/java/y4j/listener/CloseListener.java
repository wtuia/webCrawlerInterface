package y4j.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import y4j.bingimg.job.SendMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CloseListener implements ApplicationListener<ContextClosedEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(CloseListener.class);
	
	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
		logger.error("程序已关闭");
		String subject = "ERROR[app shutdown]";
		String content = "<p>程序已关闭<p>";
		try {
			content += "<p>IP地址:"+ InetAddress.getLocalHost().getHostAddress()+"</p>";
		} catch (UnknownHostException e) {
			content += e.getMessage();
		}
		content += "<p>用户:"+System.getenv("USERNAME")+"</p>";
		String[] to = new String[]{"1424471149@qq.com"};
		SendMessage.send(to,subject, content, null);
	}
}
