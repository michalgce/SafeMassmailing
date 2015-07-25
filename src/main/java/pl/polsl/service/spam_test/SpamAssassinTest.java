package pl.polsl.service.spam_test;

import com.icegreen.greenmail.util.GreenMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.enums.SpamStatus;
import pl.polsl.service.SpamAssassinService;

import javax.faces.bean.ManagedBean;
import javax.mail.internet.MimeMessage;

@Service
@ManagedBean
public class SpamAssassinTest implements SpamTest {

    @Autowired
    protected SpamAssassinService spamAssassinService;

    @Override
    public void run(final MimeMessage mimeMessage) {
        String interceptedMessage = GreenMailUtil.getWholeMessage(mimeMessage);

        spamAssassinService.setMessage(interceptedMessage);
        spamAssassinService.testMessage();
    }

    @Override
    public void flush() {
        spamAssassinService.setSpamAssassinResponseDto(null);
        spamAssassinService.setSpamStatus(null);
    }

    @Override
    public String getStatus() {
        SpamStatus spamStatus = spamAssassinService.getSpamStatus();

        switch (spamStatus) {
            case SPAM:
                return "error";
            case MAYBE_SPAM:
                return "warn";
            case NO_SPAM:
                return "ok";
        }

        return "none";
    }

    public SpamAssassinService getSpamAssassinService() {
        return spamAssassinService;
    }
}
