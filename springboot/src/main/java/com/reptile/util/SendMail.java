package com.reptile.util;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 发送邮件
 * @ClassName: SendMail  
 * @Description: TODO  
 * @author: xuesongcui
 * @date 2017年12月18日  
 *
 */
@Component
@ConfigurationProperties(prefix = "email")
public class SendMail {

	private static Logger logger = Logger.getLogger(SendMail.class);
	
	//host
	private static String mailSmtpHost; 
	//端口号
	private static String mailSmtpPort;
	//邮箱登录用户名
	private static String fromUserName;
	//邮箱应用密码
	private static String fromUserPassword;
	//收件人邮箱，如有多个收件人，使用英文逗号进行分割
	private static String toEmail;

	public static String getMailSmtpHost() {
		return mailSmtpHost;
	}

	public static void setMailSmtpHost(String mailSmtpHost) {
		SendMail.mailSmtpHost = mailSmtpHost;
	}

	public static String getMailSmtpPort() {
		return mailSmtpPort;
	}

	public static void setMailSmtpPort(String mailSmtpPort) {
		SendMail.mailSmtpPort = mailSmtpPort;
	}

	public static String getFromUserName() {
		return fromUserName;
	}

	public static void setFromUserName(String fromUserName) {
		SendMail.fromUserName = fromUserName;
	}

	public static String getFromUserPassword() {
		return fromUserPassword;
	}

	public static void setFromUserPassword(String fromUserPassword) {
		SendMail.fromUserPassword = fromUserPassword;
	}

	public static String getToEmail() {
		return toEmail;
	}

	public static void setToEmail(String toEmail) {
		SendMail.toEmail = toEmail;
	}

	/**
	 * 发送邮件
	 * @param subject 主题
	 * @param messageText 文本
	 */
	public static void sendMessage(String subject, String messageText) {
		// 第一步：配置javax.mail.Session对象
		logger.warn("为" + mailSmtpHost + "配置mail session对象");

		Properties props = new Properties();
		props.put("mail.smtp.host", mailSmtpHost);
		// 使用 STARTTLS安全连接
		props.put("mail.smtp.starttls.enable", "true");
		// 端口号
		props.put("mail.smtp.port", mailSmtpPort); 
		// 使用验证
		props.put("mail.smtp.auth", "true"); 
//		props.put("mail.debug", "true");
		Session mailSession = Session.getInstance(props, new MyAuthenticator(
				fromUserName, fromUserPassword));

		String[] emails = toEmail.split(",");

		for (int i = 0; i < emails.length; i++) {
			// 第二步：编写消息
			logger.warn("编写消息from——to:" + fromUserName + "——" + emails[i]);

			try {
				InternetAddress fromAddress = new InternetAddress(fromUserName);
				InternetAddress toAddress = new InternetAddress(emails[i]);

				MimeMessage message = new MimeMessage(mailSession);

				message.setFrom(fromAddress);
				message.addRecipient(RecipientType.TO, toAddress);

				message.setSentDate(Calendar.getInstance().getTime());
				message.setSubject(subject);
				message.setContent(messageText, "text/html;charset=utf-8");

				// 第三步：发送消息
				Transport transport = mailSession.getTransport("smtp");
				transport.connect(mailSmtpHost, fromUserName, fromUserPassword);
				Transport.send(message, message.getRecipients(RecipientType.TO));
				logger.warn("消息发送成功。from——to:" + fromUserName + "——"
						+ emails[i]);
			} catch (Exception e) {
				logger.error("消息发送失败！！！！from——to:" + fromUserName + "——"
						+ emails[i],e);
			}
		}

	}

}

class MyAuthenticator extends Authenticator {
	String userName = "";
	String password = "";

	public MyAuthenticator() {

	}

	public MyAuthenticator(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	}
	
}
