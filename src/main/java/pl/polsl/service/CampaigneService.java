package pl.polsl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.entity.Groups;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

import static org.springframework.util.StringUtils.isEmpty;

@Service
@ManagedBean
public class CampaigneService {

    protected Groups selectedGroup;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected TestService testService;


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
            clearMessageService();
            context.redirect(context.getRequestContextPath() + "/campaign/message.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToTest() {
        if (isEmpty(messageService.getTopicMessage()) || isEmpty(messageService.getContentMessage())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal",
                    "Enter topic and content."));
        }

        clearTestService();
        messageService.createMessagesToSend();

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        try {
            context.redirect(context.getRequestContextPath() + "/campaign/test.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearTestService() {
        testService.flushTests();
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

    public Groups getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(final Groups selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    protected void clearMessageService() {
        messageService.setTopicMessage(null);
        messageService.setContentMessage(null);
    }
}
