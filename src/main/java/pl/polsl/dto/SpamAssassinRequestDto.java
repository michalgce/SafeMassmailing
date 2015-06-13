package pl.polsl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpamAssassinRequestDto {

    @JsonProperty("email")
    public String message;
    @JsonProperty("options")
    public SpamAssassinOptions spamAssassinOptions;

    public SpamAssassinRequestDto(final String message, final SpamAssassinOptions options) {
        this.message = message;
        this.spamAssassinOptions = options;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public SpamAssassinOptions getSpamAssassinOptions() {
        return spamAssassinOptions;
    }

    public void setSpamAssassinOptions(final SpamAssassinOptions spamAssassinOptions) {
        this.spamAssassinOptions = spamAssassinOptions;
    }
}
