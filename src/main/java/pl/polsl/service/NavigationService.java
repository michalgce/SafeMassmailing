package pl.polsl.service;

import org.springframework.stereotype.Service;

import javax.faces.bean.ManagedBean;

@Service
@ManagedBean
public class NavigationService {

    public String goToMain() {
        return "index";
    }

    public String goToBayesSettings() {
        return "bayes";
    }

    public String goToMessage() {
        return "message";
    }

}
