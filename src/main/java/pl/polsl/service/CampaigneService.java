package pl.polsl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.polsl.dao.SubscriberDao;
import pl.polsl.entity.Groups;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

@Service
@ManagedBean
public class CampaigneService {

    @Autowired
    protected CustomJavaMailService customJavaMailService;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected SubscriberDao subscriberDao;

    public Groups selectedGroup;
    public String topicMessage;
    public String contentMessage;
    public final List<MimeMessage> preparedMessage = new ArrayList<>();

    protected JavaMailSender javaMailSender;

    public void prepareJavaMailSender() {
        javaMailSender = customJavaMailService.buildJavaMailSender();
    }

    public void goToGroup() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/campaign/groups.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToMessage() {
        if (selectedGroup == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal",
                    "Select group"));
        }

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/campaign/message.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToTest() {
        if (isEmpty(topicMessage) || isEmpty(contentMessage)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal",
                    "Enter topic and content."));
        }

        createMessagesToSend();

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/campaign/test.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void createMessagesToSend() {
        //TODO przy kazdej nowej sesji
        prepareJavaMailSender();
        preparedMessage.clear();

        subscriberDao.listGroupSubscribers(selectedGroup.getId())
                .parallelStream()
                .forEach(subscriber -> preparedMessage.add(prepareMimeMessage(topicMessage, contentMessage, subscriber.getMail())));
    }

    public void goToSend() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/campaign/send.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void goToSuccess() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/campaign/success.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send() {
        try {
            preparedMessage.parallelStream().forEach(javaMailSender::send);
        } catch (MailSendException e) {
            e.printStackTrace();
        }
        goToSuccess();
    }

    public MimeMessage prepareMimeMessage(final String title, final String content, final String recipient) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            InternetAddress internetAddress = new InternetAddress("michal@michal.pl");
            mimeMessage.setFrom(internetAddress);
            mimeMessage.setText("test Value contnet");
            mimeMessage.setRecipient(Message.RecipientType.TO, internetAddress);
            mimeMessage.setSubject("SUBJECT !");
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(mimeMessage.getContent().toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mimeMessage;

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

    public Groups getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(final Groups selectedGroup) {
        this.selectedGroup = selectedGroup;
    }
}
