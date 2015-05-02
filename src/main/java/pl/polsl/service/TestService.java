package pl.polsl.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("session")

public class TestService {
    private int counter = 0;

    @RequestMapping("/bayes")
    public String getMessage() {

        return "This is my message " + counter++;
    }
}