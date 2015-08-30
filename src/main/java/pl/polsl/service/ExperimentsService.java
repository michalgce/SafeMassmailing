package pl.polsl.service;

import com.google.api.client.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.service.spam_test.BayessTest;
import pl.polsl.service.spam_test.GmailTest;
import pl.polsl.service.spam_test.SpamAssassinTest;

import javax.faces.bean.ManagedBean;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
@ManagedBean
public class ExperimentsService {

    @Autowired
    protected TestService testService;

    public String pathDirWithSpam;
    public String pathDirWithHam;

    public void runTests() throws IOException {

        List<ExperimentResult> experimentResultList = Collections.synchronizedList(Lists.newArrayList());

        //check spam efficiency
        Files.list(Paths.get("/Users/rastabandita/Documents/praca_mgr/1000_hard_spam")).forEach(filePath -> {
            try {
                InputStream mailFileInputStream = new FileInputStream(filePath.toFile());
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session, mailFileInputStream);
                message.setFrom(new InternetAddress("michalgce@gmail.com"));
                ExperimentResult experimentResult = new ExperimentResult();
                testService.getAllAvailableSpamTest().parallelStream()
                        .forEach(spamTest -> {

                            if (spamTest instanceof BayessTest || spamTest instanceof SpamAssassinTest) {
                                spamTest.flush();
                                spamTest.run(message);

                            }

                            if (spamTest instanceof BayessTest) {
                                experimentResult.setBayesResult(spamTest.getStatus());
                            } else if (spamTest instanceof GmailTest) {
                                experimentResult.setGmailResult(spamTest.getStatus());
                            } else if (spamTest instanceof SpamAssassinTest) {
                                experimentResult.setSpamAssasinResult(spamTest.getStatus());
                            }

                            experimentResultList.add(experimentResult);
                });


            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        Long bayesResult = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getBayesResult().equals("error"))
                .count();

        Long gmailResult = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getGmailResult().equals("error"))
                .count();

        Long spamAssasinResult = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getSpamAssasinResult().equals("error"))
                .count();

        System.out.println("Bayes : " + bayesResult);
        System.out.println("Gmail : " + gmailResult);
        System.out.println("Spam Assasin : " + spamAssasinResult);

    }


    public TestService getTestService() {
        return testService;
    }

    public void setTestService(final TestService testService) {
        this.testService = testService;
    }

    public String getPathDirWithSpam() {
        return pathDirWithSpam;
    }

    public void setPathDirWithSpam(final String pathDirWithSpam) {
        this.pathDirWithSpam = pathDirWithSpam;
    }

    public String getPathDirWithHam() {
        return pathDirWithHam;
    }

    public void setPathDirWithHam(final String pathDirWithHam) {
        this.pathDirWithHam = pathDirWithHam;
    }

    protected class ExperimentResult {
        protected String spamAssasinResult;
        protected String bayesResult;
        protected String gmailResult;

        public ExperimentResult() {

        }

        public String getSpamAssasinResult() {
            return spamAssasinResult;
        }

        public void setSpamAssasinResult(final String spamAssasinResult) {
            this.spamAssasinResult = spamAssasinResult;
        }

        public String getBayesResult() {
            return bayesResult;
        }

        public void setBayesResult(final String bayesResult) {
            this.bayesResult = bayesResult;
        }

        public String getGmailResult() {
            return gmailResult;
        }

        public void setGmailResult(final String gmailResult) {
            this.gmailResult = gmailResult;
        }
    }
}
