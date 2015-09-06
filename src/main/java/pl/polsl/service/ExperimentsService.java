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
import java.io.*;
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

    public void checkAllEfficiencyOfSpams() throws IOException {
        checkEfficiencyOfSpams();
        checkEfficiencyOfHams();
    }

    public void checkEfficiencyOfSpams() throws IOException {

        List<ExperimentResult> experimentResultList = Collections.synchronizedList(Lists.newArrayList());
        final Integer counter[] = {0};
        //check spam efficiency
        Files.list(Paths.get("/Users/rastabandita/Documents/praca_mgr/zbior_testowy/spam4")).forEach(filePath -> {
            try {
                if (filePath.getFileName().toString().equals(".DS_Store")) {
                    return;
                }

                ++counter[0];

                InputStream mailFileInputStream = new FileInputStream(filePath.toFile());
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session, mailFileInputStream);
                message.setFrom(new InternetAddress("mgr.praca11@os.pl"));
                ExperimentResult experimentResult = new ExperimentResult();
                testService.getAllAvailableSpamTest().forEach(spamTest -> {

                    spamTest.flush();
                    spamTest.run(message);

                    if (spamTest instanceof BayessTest) {
                        experimentResult.setBayesResult(spamTest.getStatus());
                    } else if (spamTest instanceof GmailTest) {
                        experimentResult.setGmailResult(spamTest.getStatus());
                    } else if (spamTest instanceof SpamAssassinTest) {
                        experimentResult.setSpamAssasinResult(spamTest.getStatus());
                    }
                });

                experimentResultList.add(experimentResult);
            } catch (MessagingException | FileNotFoundException e) {
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
        PrintWriter writer = new PrintWriter("checkEfficiencyOfSpams1.txt", "UTF-8");
        writer.println("Bayes : " + bayesResult);
        writer.println("Gmail : " + gmailResult);
        writer.println("Spam Assasin : " + spamAssasinResult);
        writer.println("Bayes OK: " + bayesResult1);
        writer.println("Gmail OK: " + gmailResult1);
        writer.println("Spam Assasin OK:" + spamAssasinResult1);
        writer.close();

    }

    public void checkEfficiencyOfHams() throws IOException {

        List<ExperimentResult> experimentResultList = Collections.synchronizedList(Lists.newArrayList());
        final Integer counter[] = {0};
        //check spam efficiency
        Files.list(Paths.get("/Users/rastabandita/Documents/praca_mgr/zbior_testowy/ham4")).forEach(filePath -> {
            try {
                if (filePath.getFileName().toString().equals(".DS_Store")) {
                    return;
                }

                ++counter[0];

                InputStream mailFileInputStream = new FileInputStream(filePath.toFile());
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session, mailFileInputStream);
                message.setFrom(new InternetAddress("mgr.praca12@os.pl"));
                ExperimentResult experimentResult = new ExperimentResult();
                testService.getAllAvailableSpamTest().forEach(spamTest -> {

                    spamTest.flush();
                    spamTest.run(message);

                    if (spamTest instanceof BayessTest) {
                        experimentResult.setBayesResult(spamTest.getStatus());
                    } else if (spamTest instanceof GmailTest) {
                        experimentResult.setGmailResult(spamTest.getStatus());
                    } else if (spamTest instanceof SpamAssassinTest) {
                        experimentResult.setSpamAssasinResult(spamTest.getStatus());
                    }
                });

                experimentResultList.add(experimentResult);
            } catch (MessagingException | FileNotFoundException e) {
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
        PrintWriter writer = new PrintWriter("checkEfficiencyOfSpams1.txt", "UTF-8");
        writer.println("Bayes : " + bayesResult);
        writer.println("Gmail : " + gmailResult);
        writer.println("Spam Assasin : " + spamAssasinResult);
        writer.println("Bayes OK: " + bayesResult1);
        writer.println("Gmail OK: " + gmailResult1);
        writer.println("Spam Assasin OK:" + spamAssasinResult1);
        writer.close();
    }

    public void testHowManyEmailCanBeSendFromHostings() {
        messageService.prepareJavaMailSender();
        final int LIMIT = 10;

        long startTime = System.currentTimeMillis();

        IntStream.range(0, LIMIT).forEach((i) -> {
            long check = System.currentTimeMillis();
            MimeMessage mimeMessage = messageService.prepareMimeMessage("praca.magisterska2015@outlook.com", "Test PIPIPI", "Hello! This is test message.", "magisterska.praca@onet.pl");
            long check2 = System.currentTimeMillis();
            System.out.println("PREPARE MIME " + (check2 - check));

            long check3 = System.currentTimeMillis();
            messageService.send(mimeMessage);
            long check4 = System.currentTimeMillis();
            System.out.println("BIG SEND " + (check4 - check3));

            System.out.println("LECI PYK ! " + i);
        });


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);

    }

    public void testEfficientyForCombianedTestForSpams() throws IOException {

        final Integer[] spamCounter = {0};
        Files.list(Paths.get("/Users/rastabandita/Documents/praca_mgr/zbior_testowy/spam3")).forEach(filePath -> {
            try {

                if (filePath.getFileName().toString().equals(".DS_Store")) {
                    return;
                }

                InputStream mailFileInputStream = new FileInputStream(filePath.toFile());
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session, mailFileInputStream);
                message.setFrom(new InternetAddress("mgr.praca@serwer1429092.home.pl"));
                final Double[] counter = {0.0};
                testService.getAllAvailableSpamTest().forEach(spamTest -> {

                    spamTest.flush();
                    spamTest.run(message);

                    if (spamTest.getStatus().equals("error")) {
                        counter[0] += 1;
                    } else if (spamTest.getStatus().equals("warn")) {
                        counter[0] += 0.5;
                    }

                });

                if (counter[0] > 1.5) {
                    System.out.println("Mamy SPAM");
                    spamCounter[0] += 1;
                }


            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        System.out.println(spamCounter[0]);
    }

    public void testEfficientyForCombianedTestForHams() throws IOException {

        final Integer[] spamCounter = {0};
        Files.list(Paths.get("/Users/rastabandita/Documents/praca_mgr/zbior_testowy/ham2")).forEach(filePath -> {
            try {

                if (filePath.getFileName().toString().equals(".DS_Store")) {
                    return;
                }

                InputStream mailFileInputStream = new FileInputStream(filePath.toFile());
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session, mailFileInputStream);
                message.setFrom(new InternetAddress("mgr.praca@serwer1429092.home.pl"));
                final Double[] counter = {0.0};
                testService.getAllAvailableSpamTest().forEach(spamTest -> {

                    spamTest.flush();
                    spamTest.run(message);

                    if (spamTest.getStatus().equals("error")) {
                        counter[0] += 1;
                    } else if (spamTest.getStatus().equals("warn")) {
                        counter[0] += 0.5;
                    }

                });

                if (counter[0] > 1.5) {
                    System.out.println("Mamy SPAM");
                    spamCounter[0] += 1;
                }


            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        System.out.println(spamCounter[0]);
    }

    public void testSpeedOfSending() throws IOException {
        final Integer LIMIT = 1000;
        messageService.prepareJavaMailSender();
        MimeMessage mimeMessage =
                messageService.prepareMimeMessage("mgr.praca@michalgce.ehost.pl", "Test Speed Message :)", "Hello What's UP!!!!", "mgr.praca@o2.pl");

        long startTime = System.currentTimeMillis();
        IntStream.range(0, LIMIT).forEach(value -> {
            try {
                mimeMessage.setSubject(value + " " + mimeMessage.getSubject());
                messageService.send(mimeMessage);
                System.out.println(value);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Czas wysyłania wiadomości : ");
        System.out.println(System.currentTimeMillis() - startTime);
    }

    public void testSpeedOfSendingParallel() throws IOException {
        final Integer LIMIT = 1000;
        messageService.prepareJavaMailSender();
        MimeMessage mimeMessage =
                messageService.prepareMimeMessage("mgr.praca@michalgce.ehost.pl", "Test Speed Message :)", "Hello What's UP!!!!", "mgr.praca@o2.pl");


        long startTime = System.currentTimeMillis();
        IntStream.range(0, LIMIT).parallel().forEach(value -> {
            try {
                mimeMessage.setSubject(value + " " + "This is test message from TOKIO. How are you.");
                messageService.send(mimeMessage);
                System.out.println(value);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Czas wysyłania wiadomości : ");
        System.out.println(System.currentTimeMillis() - startTime);
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
