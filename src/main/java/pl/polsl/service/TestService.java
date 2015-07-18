package pl.polsl.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.polsl.service.spam_test.SpamTest;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;

@Service
@ManagedBean
public class TestService {

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected CustomJavaMailService customJavaMailService;

    @Autowired
    protected List<SpamTest> allAvailableSpamTest;

    public void runTests() {
        MimeMessage mimeMessage = prepareMessageToTests();

        // parallel test run
        allAvailableSpamTest.parallelStream().peek(spamTest1 ->  {
            System.out.println("Run test " + spamTest1.getClass().getName());
        }).forEach(spamTest -> spamTest.run(mimeMessage));
    }

    public void flushTests() {
        allAvailableSpamTest.parallelStream().peek(spamTest -> {
            System.out.println("Flush test " + spamTest.getClass().getName());
        }).forEach(SpamTest::flush);
    }
    protected MimeMessage prepareMessageToTests() {
        //TODO zamiast mockowac serwer mozna wysylac maila na mailTrap i pobierac go z tamtad
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

            return received[0];

        }

        throw new IllegalStateException("There was a problem with preparing message to tests.");
    }
}
