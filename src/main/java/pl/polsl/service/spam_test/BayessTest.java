package pl.polsl.service.spam_test;

import com.icegreen.greenmail.util.GreenMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.service.bayess.BayessService;

import javax.faces.bean.ManagedBean;
import javax.mail.internet.MimeMessage;

@Service
@ManagedBean
public class BayessTest implements SpamTest {

    protected Boolean isThisSpam;

    @Autowired
    protected BayessService bayessService;

    @Override
    public void run(final MimeMessage mimeMessage) {
        String interceptedMessage = GreenMailUtil.getWholeMessage(mimeMessage);

        isThisSpam = bayessService.analyze(interceptedMessage);
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
