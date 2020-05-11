package message;

import bingimg.bean.MailBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class SendMessage {
	
	private static final Logger logger = LoggerFactory.getLogger(SendMessage.class);
    private static Properties properties;
    private static String TRANS_PORT;
    private static String HOST;
    private static String CHARSET;
    private static String ENCODING;
    private static String TYPE;
    private static String ADDR;
    private static String PASS;
    private SendMessage(){}

    private static void lodProperties() {
        if (properties == null) {
            String filePath = "y4j.properties";
            try (FileInputStream fis = new FileInputStream(filePath)){
                properties = new Properties();
                properties.load(fis);
                TRANS_PORT = properties.getProperty("session.transport");
                HOST = properties.getProperty("transport.host");
                TYPE = properties.getProperty("content.text.type");
                CHARSET = properties.getProperty("mail.file.charset");
                ENCODING = "B";
                ADDR = properties.getProperty("addr");
                PASS = properties.getProperty("pass");
            }catch (Exception e) {
                logger.error("读取邮件参数异常");
            }
        }
    }

	/*public static void main(String[] args) {
		String subject = "邮件标题";
		String content = "邮件正文";
		String[] files = new String[]{"C:\\Users\\14244\\Desktop\\LNTIL-MA-CMNET-BAS07-CTLJME60X"};
		String to[] = new String[]{"1424471149@qq.com"};
		SendMessage.send(to,subject, content, files);
	}*/

	public static void  send(String[] to, String subject, String content, String[] files){
		MailBean bean = new MailBean(subject, content, files, to);
		send(bean);
	}

    private static void send(MailBean mail) {
        try {
			lodProperties();
        	beanRequireNonNull(mail);
			// 建立会话
			Session session = Session.getInstance(properties);
			// 建立信息
			Message message = new MimeMessage(session);
            // 发件人地址
            message.setFrom(new InternetAddress(ADDR));
            // 创建发送，抄送，密送，用户
            create(message, mail.getTo(), Message.RecipientType.TO);
            create(message, mail.getCc(), Message.RecipientType.CC);
            create(message, mail.getBcc(), Message.RecipientType.BCC);
            // 设置正文内容
            BodyPart bodyPart = setMailContentBody(message, mail);
            // 邮件正文框(包括附件)
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            // 设置附件
            addFile(mail.getFiles(), multipart);
            message.setContent(multipart);
            // 服务器验证 连接
            Transport transport = session.getTransport(TRANS_PORT);
            transport.connect(HOST, ADDR, PASS);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            logger.info("邮件发送成功");
        } catch (Exception e) {
            logger.error("邮件发送失败", e);
        }
    }

    private static void beanRequireNonNull(MailBean bean) {
        Objects.requireNonNull(bean.getTo(),"收件人不能为空");
        Objects.requireNonNull(PASS, "密码不能为空");
		Objects.requireNonNull(ADDR,"发件人不能为空");
    }

    // 创建发送用户
    private static void create(Message message, String[] send, Message.RecipientType type)
			throws Exception {
        if (send != null) {
            String sends = getMails(send);
            Address[] address = InternetAddress.parse(sends);
            message.setRecipients(type, address);
        }
    }

    // 读取用户
    private static String getMails(String[] mails) {
        StringBuilder sb = new StringBuilder();
        for (String str : mails) {
            sb.append(str).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private static BodyPart setMailContentBody(Message message, MailBean mailBean) {
        // 邮件组成
        BodyPart bodyPart = new MimeBodyPart();
        try {
            // 发送日期
            message.setSentDate(new Date());
            // 发送主题
            message.setSubject(mailBean.getSubject());
            // 发送内容，邮件正文内容
            message.setText(mailBean.getContent());
            bodyPart.setContent(mailBean.getContent(), TYPE);
        }catch (Exception e) {
            logger.error("添加正文异常", e);
        }
        return bodyPart;
    }

    // 添加附件
    private static void addFile(String[] fileList, Multipart multipart) {
        if (fileList == null || fileList.length <= 0) {
            return;
        }
        try {
            for (String fileName : fileList) {
                BodyPart mimeBodyPart = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(fileName);
                mimeBodyPart.setDataHandler(new DataHandler(fileDataSource));
                mimeBodyPart.setFileName(
                        MimeUtility.encodeText(fileDataSource.getName(), CHARSET, ENCODING));
                multipart.addBodyPart(mimeBodyPart);
            }
        }catch (Exception e) {
           	logger.error("添加附件异常", e);
        }
    }

}
