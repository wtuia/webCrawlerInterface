package bingimg.bean;

import java.util.Arrays;

/**
 *
 *
 * address - 地址,
 * to - 收件人,
 * cc - 抄送,
 * bcc - 密送,
 * subject - 主题,
 * content - 内容,
 * fileList - 附件
 *
 */
public class MailBean {


    private String address; // 邮箱
    private String user; // 用户 默认是邮箱
    private String password; // 密码

    private String[] to; // 发送
    private String[] cc; // 抄送
    private String[] bcc; // 密送

    private String subject; // 标题
    private String content; // 正文

    private String[] files; // 附件

    public MailBean( String subject, String content, String[] files, String[] to) {
        this.subject = subject;
        this.content = content;
		this.to = to;
        this.files = files;
        // 默认用户名与邮箱名相同
    }

	public MailBean(String address, String password, String[] to, String[] cc,
					String[] bcc, String subject, String content, String[] files) {
		this.address = address;
		this.user = address;
		this.password = password;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.content = content;
		this.files = files;
	}

    public String[] getTo() {
        return to;
    }

    public String[] getCc() {
        return cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String[] getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "MainBean{" +
                "address='" + address + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", to=" + Arrays.toString(to) +
                ", cc=" + Arrays.toString(cc) +
                ", bcc=" + Arrays.toString(bcc) +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", files=" + Arrays.toString(files) +
                '}';
    }
}
