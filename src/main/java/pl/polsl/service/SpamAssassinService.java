package pl.polsl.service;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.polsl.dto.SpamAssassinOptions;
import pl.polsl.dto.SpamAssassinRequestDto;
import pl.polsl.dto.SpamAssassinResponseDto;
import pl.polsl.enums.SpamStatus;

import javax.faces.bean.ManagedBean;
import java.time.temporal.ValueRange;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@ManagedBean
public class SpamAssassinService {

    @Value("${spamassassin.url}")
    protected String spamAssassinUrl;

    protected String message;

    protected SpamAssassinResponseDto spamAssassinResponseDto;

    protected SpamStatus spamStatus;

    public void testMessage() {
        resetResponse();

        if (isEmpty(message)) {
            throw new IllegalStateException("Insert message before try to test it.");
        }

        SpamAssassinRequestDto spamAssassinRequestDto = new SpamAssassinRequestDto(message, SpamAssassinOptions.LONG);

        RestTemplate restTemplate = new RestTemplate();
        spamAssassinResponseDto = restTemplate.postForObject(spamAssassinUrl, spamAssassinRequestDto, SpamAssassinResponseDto.class);

        Double score = spamAssassinResponseDto.getScore();

        if (ValueRange.of(0, 1).isValidIntValue(score.longValue())) {
            spamStatus = SpamStatus.NO_SPAM;
        } else if (ValueRange.of(2, 4).isValidIntValue(score.longValue())){
            spamStatus = SpamStatus.MAYBE_SPAM;
        } else {
            spamStatus = SpamStatus.SPAM;
        }

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

    public SpamStatus getSpamStatus() {
        return spamStatus;
    }

    public void setSpamStatus(final SpamStatus spamStatus) {
        this.spamStatus = spamStatus;
    }
}
