package pl.polsl.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

@Service
@ManagedBean
public class TestService {

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected CustomJavaMailService customJavaMailService;

    @Autowired
    protected SpamAssassinService spamAssassinService;

    public void testCreatedMessage() {
        JavaMailSender javaMailSender = customJavaMailService.buildJavaTestMailSender();
        GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);
        Optional<MimeMessage> firstAvailableMsg = messageService.getPreparedMessage().stream().findFirst();

        if (firstAvailableMsg != null) {
            greenMail.start();
            javaMailSender.send(firstAvailableMsg.get());
            MimeMessage[] received = greenMail.getReceivedMessages();
            greenMail.stop();
            if (received.length < 1) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", "System cannot intercept message."));
            }

            String interceptedMessage = GreenMailUtil.getWholeMessage(received[0]);
            runSpamAssasinTest(interceptedMessage);

        }

    }

    protected void runSpamAssasinTest(final String interceptedMessage) {
        spamAssassinService.setMessage(interceptedMessage);
        spamAssassinService.testMessage();
    }

}
