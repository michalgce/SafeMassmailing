package pl.polsl.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SpamAssassinOptions {
    SHORT("short"), LONG("long");

    public String text;

    SpamAssassinOptions(final String s) {
        this.text = s;
    }

    @JsonValue
    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
