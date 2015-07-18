package pl.polsl.service.spam_test;

import javax.mail.internet.MimeMessage;

public interface SpamTest {

    void run(MimeMessage mimeMessage);

    void flush();
}
