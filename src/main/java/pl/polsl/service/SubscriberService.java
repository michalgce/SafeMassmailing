package pl.polsl.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.polsl.dao.SubscriberDao;
import pl.polsl.entity.Subscriber;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.persistence.RollbackException;
import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isEmpty;


@Service
@ManagedBean
public class SubscriberService {

    @Autowired
    protected SubscriberDao subscriberDao;

    public List<Subscriber> subscribers;

    protected String firstName;
    protected String lastName;
    protected String mail;

    @PostConstruct
    public void fetchSubscribersFromDB() {
        subscribers = Lists.newArrayList(subscriberDao.findAll());
    }

    @Transactional
    public void addSubscriber() {
        Boolean succes = false;
        if (isEmpty(firstName) || isEmpty(lastName) || isEmpty(mail)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", "Insert firstname, lastname and mail."));
        }
        Subscriber subscriber = new Subscriber(firstName, lastName, mail);
        try {
            subscriberDao.save(subscriber);
            fetchSubscribersFromDB();
            succes = true;
        } catch (DataAccessException e) {
            succes = false;
            System.out.println("Entered email address already exists in DB.");
        } catch (RollbackException e) {
            succes = false;
        } finally {
            if (succes) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Subscriber has been added to your DB"));
            } else {
                //TODO nie wyswietla erroru :(
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", "Subscriber hasn't been added to your DB"));
            }
        }

    }

    public void reset() {
        firstName = null;
        lastName = null;
        mail = null;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(final List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(final String mail) {
        this.mail = mail;
    }
}
