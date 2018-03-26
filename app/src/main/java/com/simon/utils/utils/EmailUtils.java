package com.simon.utils.utils;

import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 邮件
 * Created by Administrator on 2018/3/26.
 */
@SuppressWarnings("all")
public class EmailUtils {

    private EmailUtils() {
    }

    public static EmailUtils getInstance() {
        return SafeMode.mEmailUtil;
    }

    public static class SafeMode {
        private static final EmailUtils mEmailUtil = new EmailUtils();
    }

    /**
     * 发送邮件
     *
     * @param fromEmailAddress
     * @param fromEmailPassword
     * @param toEmailAddress
     * @param Subject
     * @param Content
     * @return
     */
    public boolean sendEmailMessages(String fromEmailAddress, String fromEmailPassword, String toEmailAddress, String Subject, String Content) {// 配置发送及接收邮箱
        try {
            Properties props = new Properties();
            props.put("mail.smtp.protocol", "smtp");
            props.put("mail.smtp.auth", "true");//设置要验证
            props.put("mail.smtp.host", "smtp.exmail.qq.com");//设置host
            props.put("mail.smtp.port", "465");  //设置端口
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            PassAuthenticator pass = new PassAuthenticator(fromEmailAddress, fromEmailPassword);   //获取帐号密码
            Session session = Session.getInstance(props, pass); //获取验证会话
            // 始发邮箱
            InternetAddress fromAddress = new InternetAddress(fromEmailAddress, fromEmailAddress);
            // 目标邮箱
            InternetAddress toAddress = new InternetAddress(toEmailAddress, toEmailAddress);
            // 配置发送信息
            MimeMessage message = new MimeMessage(session);
            MimeMultipart allMultipart = new MimeMultipart("mixed"); // 附件
            message.setContent(allMultipart); // 发邮件时添加附件
            message.setSubject(Subject);
            message.setText(Content, "UTF-8");
            message.setFrom(fromAddress);
            message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
            message.saveChanges();
            // 连接邮箱并发送
            Transport transport = session.getTransport("smtp");
            // 始发邮箱账号密码
            transport.connect("smtp.exmail.qq.com", fromEmailAddress, fromEmailPassword);
            transport.send(message);
            transport.close();
            return true;
        } catch (Exception e) {
            Log.i("Simon", "sendEmailMessages Exception = " + e.getMessage());
        }
        return false;
    }

    /**
     * 发送邮件-附件
     *
     * @param fromEmailAddress
     * @param fromEmailPassword
     * @param toEmailAddress
     * @param Subject
     * @param Content
     * @param files
     * @return
     */
    public boolean sendEmailMessagesWithFiles(String fromEmailAddress, String fromEmailPassword, String toEmailAddress, String Subject, List<File> files) {
        Properties props = new Properties();
        props.put("mail.smtp.protocol", "smtp");
        props.put("mail.smtp.auth", "true");//设置要验证
        props.put("mail.smtp.host", "smtp.exmail.qq.com");//设置host
        props.put("mail.smtp.port", "465");  //设置端口
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        PassAuthenticator pass = new PassAuthenticator(fromEmailAddress, fromEmailPassword);   //获取帐号密码
        Session session = Session.getInstance(props, pass); //获取验证会话
        try {
            //配置发送及接收邮箱
            InternetAddress fromAddress, toAddress;
            // 发件邮箱
            fromAddress = new InternetAddress(fromEmailAddress, fromEmailAddress);
            // 收件邮箱
            toAddress = new InternetAddress(toEmailAddress, toEmailAddress);

            // 附件与内容 只能二选一
            MimeMessage message = new MimeMessage(session);
            MimeMultipart allMultipart = new MimeMultipart("mixed"); //附件
            for (File file : files) {
                MimeBodyPart attachPart = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(file); //打开要发送的文件
                attachPart.setDataHandler(new DataHandler(fds));
                attachPart.setFileName(MimeUtility.encodeText(file.getName()));//TODO
                // allMultipart.addBodyPart(attachPart);//添加
            }
            message.setContent(allMultipart); //发邮件时添加附件

            message.setSubject(Subject);
            message.setFrom(fromAddress);
            message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
            message.saveChanges();
            // 连接邮箱并发送
            Transport transport = session.getTransport("smtp");
            // 发送邮件的账号和密码
            transport.connect("smtp.exmail.qq.com", fromEmailAddress, fromEmailPassword);
            transport.send(message);
            transport.close();
        } catch (Exception e) {
            Log.i("Simon", "sendEmailMessagesWithFiles Exception = " + e.getMessage());
        }
        return false;
    }

    class PassAuthenticator extends Authenticator {
        String username;
        String password;

        public PassAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            // 添加发送邮箱的账号和密码
            return new PasswordAuthentication(username, password);
        }
    }

}
