package pl.polsl.service.spam_test;

import com.icegreen.greenmail.util.GreenMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.service.SpamAssassinService;

import javax.mail.internet.MimeMessage;

@Service
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

    public SpamAssassinService getSpamAssassinService() {
        return spamAssassinService;
    }
}
