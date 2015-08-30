package pl.polsl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.polsl.dao.SubscriberDao;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

@Service
@ManagedBean
public class MessageService {

    protected JavaMailSender javaMailSender;

    protected String fromAddress;
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
        if (isBlank(fromAddress)) {

            //TODO obsługa wyjątków ładna!!!
            //throw new Exception("From address is obligatory to send messages!");

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Fatal", "From address is obligatory to send messages!"));

            //TODO tmp resolve
            return null;
        }

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            //TODO adres from musi byc aktualnym nadawca!!
            mimeMessage.setFrom(new InternetAddress(fromAddress));
            mimeMessage.setContent(content, "text/html; charset=utf-8");
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setSubject(title);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return mimeMessage;
    }

    public void send() {
        try {
            preparedMessage.parallelStream().forEach(javaMailSender::send);
        } catch (MailSendException e) {
            e.printStackTrace();
        }

    }

    public void send(final MimeMessage mimeMessage) {
        if (javaMailSender == null) {
            prepareJavaMailSender();
        }

        javaMailSender.send(mimeMessage);
    }

    protected void createMessagesToSend() {
        //TODO przy kazdej nowej sesji
        prepareJavaMailSender();
        preparedMessage.clear();

        subscriberDao.listGroupSubscribers(campaigneService.selectedGroup.getId())
                .parallelStream()
                .forEach(subscriber -> preparedMessage.add(prepareMimeMessage(topicMessage, contentMessage, subscriber.getMail())));
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

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(final String fromAddress) {
        this.fromAddress = fromAddress;
    }
}
