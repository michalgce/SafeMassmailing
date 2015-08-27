/*
package pl.polsl.service.spam_test;

import com.mailjet.api.client.MailJetApiCallException;
import com.mailjet.api.client.MailJetApiClient;
import com.mailjet.api.client.model.ResultSet;
import com.mailjet.api.model.v3.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class MailjetTest {

    @Autowired
    protected MailJetApiClient mailJetApiClient;

    @Override
    public void run(final MimeMessage mimeMessage) {
        try {
            ResultSet<Message> execute = mailJetApiClient
                    .createCall(Message.List)
                    .execute();

            execute.iterator().forEachRemaining(message -> {
                System.out.println(message.getID() + " : " + message.getSpamassassinScore());
            });

            System.out.println("TESTY");

        } catch (MailJetApiCallException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void flush() {

    }

    @Override
    public String getStatus() {
        return null;
    }
}
*/
