package pl.polsl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import pl.polsl.dao.SubscriberDao;
import pl.polsl.entity.Groups;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

@Service
@ManagedBean
public class CampaigneService {

    @Autowired
    protected JavaMailSender javaMailSender;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected SubscriberDao subscriberDao;

    public Groups selectedGroup;
    public String topicMessage;
    public String contentMessage;
    public final List<MimeMessagePreparator> preparedMessage = new ArrayList<>();

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
        preparedMessage.clear();
        subscriberDao.listGroupSubscribers(selectedGroup.getId())
                .parallelStream()
                .forEach(subscriber -> preparedMessage.add(prepareMessage(topicMessage, contentMessage, subscriber.getMail())));
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

        preparedMessage.parallelStream().forEach(javaMailSender::send);

        goToSuccess();
    }

    public MimeMessagePreparator prepareMessage(final String title, final String content, final String recipient) {
        return mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom("michalgce@gmail.com");
            mimeMessageHelper.setSubject(title);
            mimeMessageHelper.setText(content);
            mimeMessageHelper.setTo(recipient);
        };
    }

    public MimeMessage prepareMimeMessage(final String title, final String content, final String recipient) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

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
