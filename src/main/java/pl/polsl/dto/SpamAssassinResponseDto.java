package pl.polsl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpamAssassinResponseDto {

    public Boolean success;
    public String message;
    public String report;
    public Double score;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(final Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(final Double score) {
        this.score = score;
    }

    public String getReport() {
        return report;
    }

    public void setReport(final String report) {
        this.report = report;
    }
}
