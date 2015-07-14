package pl.polsl.service;

import com.icegreen.greenmail.util.ServerSetupTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import pl.polsl.dao.SMTPConfigurationDao;
import pl.polsl.entity.SMTPConfiguration;

import java.util.Properties;

@Component
public class CustomJavaMailService {

    @Autowired
    protected SMTPConfigurationDao smtpConfigurationDao;

    public JavaMailSender buildJavaMailSender() {
        SMTPConfiguration smtpConfiguration = smtpConfigurationDao.getActive();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setUsername(smtpConfiguration.getUserName());
        mailSender.setPassword(smtpConfiguration.getPassword());
        mailSender.setHost(smtpConfiguration.getAddress());
        mailSender.setPort(smtpConfiguration.getPort());
        mailSender.setProtocol("smtp");
        return mailSender;
    }


    public JavaMailSender buildJavaTestMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setUsername("test@mail.extern");
        mailSender.setHost("localhost");
        mailSender.setPort(ServerSetupTest.SMTP.getPort());
        mailSender.setProtocol("smtp");
        return mailSender;
    }

}
