package pl.polsl.service.spam_test;

import com.icegreen.greenmail.util.GreenMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.service.bayess.BayessService;

import javax.faces.bean.ManagedBean;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Service
@ManagedBean
public class BayessTest implements SpamTest {

    protected Boolean isThisSpam;

    @Autowired
    protected BayessService bayessService;

    @Override
    public void run(final MimeMessage mimeMessage) {
        String interceptedMessage = GreenMailUtil.getWholeMessage(mimeMessage);
        String plainMessage = null;
        try {
            String subject = mimeMessage.getSubject();
            if (subject == null) {
                subject = "";
            }

            plainMessage = "Subject: " + subject + "\n" + mimeMessage.getContent().toString();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isThisSpam = bayessService.analyze(plainMessage);
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

    public Boolean getIsThisSpam() {
        return isThisSpam;
    }

    public void setIsThisSpam(final Boolean isThisSpam) {
        this.isThisSpam = isThisSpam;
    }

    public BayessService getBayessService() {
        return bayessService;
    }

    public void setBayessService(final BayessService bayessService) {
        this.bayessService = bayessService;
    }
}
