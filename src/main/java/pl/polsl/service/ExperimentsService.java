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
import java.util.stream.IntStream;

@Service
@ManagedBean
public class ExperimentsService {

    @Autowired
    protected TestService testService;

    @Autowired
    protected MessageService messageService;

    public String pathDirWithSpam;
    public String pathDirWithHam;

    public void checkEfficiencyOfSpams() throws IOException {

        List<ExperimentResult> experimentResultList = Collections.synchronizedList(Lists.newArrayList());

        //check spam efficiency
        Files.list(Paths.get("/Users/rastabandita/Documents/praca_mgr/zbior_testowy/spam")).forEach(filePath -> {
            try {

                if (filePath.getFileName().toString().equals(".DS_Store")) {
                    return;
                }

                InputStream mailFileInputStream = new FileInputStream(filePath.toFile());
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session, mailFileInputStream);
                message.setFrom(new InternetAddress("mgr.praca@o2.pl"));
                ExperimentResult experimentResult = new ExperimentResult();
                testService.getAllAvailableSpamTest().parallelStream()
                        .forEach(spamTest -> {

                            if (spamTest instanceof GmailTest) {
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

                });

                experimentResultList.add(experimentResult);


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

        Long bayesResult1 = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getBayesResult().equals("ok"))
                .count();
        Long gmailResult1 = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getGmailResult().equals("ok"))
                .count();
        Long spamAssasinResult1 = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getSpamAssasinResult().equals("ok"))
                .count();

        System.out.println("Bayes OK : " + bayesResult1);
        System.out.println("Gmail OK : " + gmailResult1);
        System.out.println("Spam Assasin OK : " + spamAssasinResult1);

    }

    public void checkEfficiencyOfHams() throws IOException {

        List<ExperimentResult> experimentResultList = Collections.synchronizedList(Lists.newArrayList());

        //check spam efficiency
        Files.list(Paths.get("/Users/rastabandita/Documents/praca_mgr/zbior_testowy/ham")).forEach(filePath -> {
            try {

                if (filePath.getFileName().toString().equals(".DS_Store")) {
                    return;
                }

                InputStream mailFileInputStream = new FileInputStream(filePath.toFile());
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session, mailFileInputStream);
                message.setFrom(new InternetAddress("mgr.praca@o2.pl"));
                ExperimentResult experimentResult = new ExperimentResult();
                testService.getAllAvailableSpamTest().parallelStream()
                        .forEach(spamTest -> {

                            if (spamTest instanceof GmailTest) {
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

                        });

                experimentResultList.add(experimentResult);


            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        Long bayesResult = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getBayesResult().equals("ok"))
                .count();
        Long gmailResult = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getGmailResult().equals("ok"))
                .count();
        Long spamAssasinResult = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getSpamAssasinResult().equals("ok"))
                .count();

        System.out.println("Bayes OK : " + bayesResult);
        System.out.println("Gmail OK : " + gmailResult);
        System.out.println("Spam Assasin OK : " + spamAssasinResult);

        Long bayesResult1 = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getBayesResult().equals("error"))
                .count();
        Long gmailResult1 = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getGmailResult().equals("error"))
                .count();
        Long spamAssasinResult1 = experimentResultList
                .stream()
                .filter(experimentResult -> experimentResult.getSpamAssasinResult().equals("error"))
                .count();

        System.out.println("Bayes error : " + bayesResult1);
        System.out.println("Gmail error : " + gmailResult1);
        System.out.println("Spam Assasin error : " + spamAssasinResult1);
    }

    public void testHowManyEmailCanBeSendFromHostings() {
        messageService.prepareJavaMailSender();
        final int LIMIT = 1000;

        long startTime = System.currentTimeMillis();

        IntStream.range(0, LIMIT).forEach((i) -> {
            MimeMessage mimeMessage = messageService.prepareMimeMessage("mgr.praca@o2.pl", "Test PIPIPI", "Hello! This is test message.", "mgr.praca@o2.pl");
            try {
                mimeMessage.setFrom(new InternetAddress("mgr.praca@o2.pl"));
                messageService.send(mimeMessage);
                System.out.println("LECI PYK ! " + i);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);

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
