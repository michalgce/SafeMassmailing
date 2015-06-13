package pl.polsl.controller;


import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class MailingErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/start2.xhtml";
    }
}
