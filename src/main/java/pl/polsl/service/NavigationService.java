package pl.polsl.service;

import org.springframework.stereotype.Service;

@Service
public class NavigationService {

    public String goToMain() {
        return "index";
    }

    public String goToBayesSettings() {
        return "bayes";
    }

}
