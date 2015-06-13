package pl.polsl.service;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.polsl.dto.SpamAssassinOptions;
import pl.polsl.dto.SpamAssassinRequestDto;
import pl.polsl.dto.SpamAssassinResponseDto;

import javax.faces.bean.ManagedBean;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@ManagedBean
public class SpamAssassinService {

    @Value("${spamassassin.url}")
    private String spamAssassinUrl;

    private String message;

    private SpamAssassinResponseDto spamAssassinResponseDto;

    public void testMessage() {
        resetResponse();

        if (isEmpty(message)) {
            throw new IllegalStateException("Insert message before try to test it.");
        }

        SpamAssassinRequestDto spamAssassinRequestDto = new SpamAssassinRequestDto(message, SpamAssassinOptions.LONG);

        RestTemplate restTemplate = new RestTemplate();
        spamAssassinResponseDto = restTemplate.postForObject(spamAssassinUrl, spamAssassinRequestDto, SpamAssassinResponseDto.class);

    }

    private void resetResponse() {
        spamAssassinResponseDto = null;
        RequestContext.getCurrentInstance().update("spamAssassinReportGrid");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public SpamAssassinResponseDto getSpamAssassinResponseDto() {
        return spamAssassinResponseDto;
    }

    public void setSpamAssassinResponseDto(final SpamAssassinResponseDto spamAssassinResponseDto) {
        this.spamAssassinResponseDto = spamAssassinResponseDto;
    }
}
