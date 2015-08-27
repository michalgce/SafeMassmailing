package pl.polsl.service.spam_test;

import com.google.api.services.gmail.Gmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.polsl.service.GmailServiceFactory;
import pl.polsl.service.MessageService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
@ManagedBean
public class GmailTest implements SpamTest {

    protected static final String gmailQueryAllUnRead = "in:anywhere";

    @Value("${gmail.testmail}")
    protected String gmailTestMail;

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected GmailServiceFactory gmailServiceFactory;

    protected Gmail gmail;

    protected Boolean isThisSpam;

    @PostConstruct
    protected void init() {
        try {
            gmail = gmailServiceFactory.getGmailService();
        } catch (IOException e) {
            //TODO obsługiwać ładnie wyjątki
            e.printStackTrace();
        }
    }

    @Override
    public void run(final MimeMessage mimeMessage) {
        isThisSpam = null;

        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());

        //TODO send mail via current smtp
        try {
            deleteAllUnReadMail(gmail);

            UUID uuid = UUID.randomUUID();
            mimeMessage.addHeader("Test-Mail-UUID", uuid.toString());
            sendTestEmail(mimeMessage);

            String gmailQuery = "in:anywhere after: "
                    + localCalendar.get(Calendar.YEAR)
                    + "/"
                    + (localCalendar.get(Calendar.MONTH) + 1)
                    + "/"
                    + (localCalendar.get(Calendar.DAY_OF_MONTH) - 1);

            TimeUnit.SECONDS.sleep(5);

            List<com.google.api.services.gmail.model.Message> messages = gmail.users()
                    .messages()
                    .list(gmailTestMail)
                    .setQ(gmailQuery)
                    .execute().getMessages();

            if (!isEmpty(messages)) {
             messages.forEach(message -> {
                 com.google.api.services.gmail.model.Message messageEntry = null;
                 try {
                     messageEntry = gmail.users().messages().get(gmailTestMail, message.getId()).execute();
                     final List<String> labelIds = messageEntry.getLabelIds();
                     messageEntry.getPayload().getHeaders()
                             .stream()
                             .filter(header -> header.getName().equals("Test-Mail-UUID"))
                             .forEach(header -> {
                                 if (header.getValue().equals(uuid.toString())) {
                                     if (labelIds.contains("SPAM")) {
                                         isThisSpam = true;
                                     }
                                 isThisSpam = false;
                         }
                     });

                 } catch (IOException e) {
                     //TODO obsługiwać ładnie wyjątki
                     e.printStackTrace();
                 }
             });
            }

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        isThisSpam = null;
    }


    @Override
    public String getStatus() {
        if (isThisSpam == null) {
            return "none";
        }

        if (isThisSpam) {
            return "error";
        }

        return "ok";
    }

    protected void sendTestEmail(final MimeMessage mimeMessage) {
        try {
            //TODO adres from musi byc aktualnym nadawca!!
            mimeMessage.setContent(mimeMessage.getContent(), "text/html; charset=utf-8");
            mimeMessage.setRecipients(Message.RecipientType.TO, gmailTestMail);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageService.send(mimeMessage);
    }

    protected void deleteAllUnReadMail(final Gmail gmail) {
        try {
            List<com.google.api.services.gmail.model.Message> messages = gmail.users()
                    .messages()
                    .list(gmailTestMail)
                    .setQ(gmailQueryAllUnRead)
                    .execute().getMessages();

            messages.forEach(message -> {

                try {
                    gmail.users().messages().delete(gmailTestMail, message.getId()).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Deleated all unread messages from GMAIL");
    }

    public Boolean getIsThisSpam() {
        return isThisSpam;
    }
}
