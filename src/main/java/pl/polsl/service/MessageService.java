package pl.polsl.service;

import org.springframework.stereotype.Service;

import javax.faces.bean.ManagedBean;

@Service
@ManagedBean
public class MessageService {

    public String text;

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public void sendMessage() {
        //TODO
    }
}
