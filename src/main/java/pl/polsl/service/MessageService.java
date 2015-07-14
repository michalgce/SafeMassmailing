package pl.polsl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.polsl.dao.SubscriberDao;

import javax.faces.bean.ManagedBean;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@ManagedBean
public class MessageService {

    protected JavaMailSender javaMailSender;

    protected String topicMessage;
    protected String contentMessage;

    protected final List<MimeMessage> preparedMessage = new ArrayList<>();

    @Autowired
    protected CampaigneService campaigneService;

    @Autowired
    protected SubscriberDao subscriberDao;

    @Autowired
    protected CustomJavaMailService customJavaMailService;

    public void prepareJavaMailSender() {
        javaMailSender = customJavaMailService.buildJavaMailSender();
    }

    public MimeMessage prepareMimeMessage(final String title, final String content, final String recipient) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            InternetAddress internetAddress = new InternetAddress(recipient);
            mimeMessage.setFrom(internetAddress);
            mimeMessage.setText(content);
            mimeMessage.setRecipient(Message.RecipientType.TO, internetAddress);
            mimeMessage.setSubject(title);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return mimeMessage;
    }

    protected void createMessagesToSend() {
        //TODO przy kazdej nowej sesji
        prepareJavaMailSender();
        preparedMessage.clear();

        subscriberDao.listGroupSubscribers(campaigneService.selectedGroup.getId())
                .parallelStream()
                .forEach(subscriber -> preparedMessage.add(prepareMimeMessage(topicMessage, contentMessage, subscriber.getMail())));
    }

    public void send() {
        try {
            preparedMessage.parallelStream().forEach(javaMailSender::send);
        } catch (MailSendException e) {
            e.printStackTrace();
        }

    }

    public String getTopicMessage() {
        return topicMessage;
    }

    public void setTopicMessage(final String topicMessage) {
        this.topicMessage = topicMessage;
    }

    public String getContentMessage() {
        return contentMessage;
    }

    public void setContentMessage(final String contentMessage) {
        this.contentMessage = contentMessage;
    }

    public List<MimeMessage> getPreparedMessage() {
        return preparedMessage;
    }
}
