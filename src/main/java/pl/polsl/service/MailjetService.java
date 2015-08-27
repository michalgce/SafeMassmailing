package pl.polsl.service;

import com.mailjet.api.client.MailJetApiClient;
import com.mailjet.api.client.config.MailJetClientConfiguration;
import com.mailjet.api.client.config.MailJetClientConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MailjetService {

    @Bean
    public MailJetApiClient buildClient() {
        try {
            MailJetClientConfiguration config = new MailJetClientConfiguration()
                    .loadFromClassPath("my_mailjet.properties");

            return config.buildClient();
        } catch (MailJetClientConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

}
