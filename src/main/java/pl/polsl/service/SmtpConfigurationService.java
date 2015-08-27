package pl.polsl.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.dao.SMTPConfigurationDao;
import pl.polsl.dao.SubscriberDao;
import pl.polsl.entity.SMTPConfiguration;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
@ManagedBean
public class SmtpConfigurationService {

    @Autowired
    protected SMTPConfigurationDao smtpConfigurationDao;

    @Autowired
    protected SubscriberDao subscriberDao;

    protected String name;
    protected String address;
    protected Integer port;
    protected String userName;
    protected String password;

    protected List<SMTPConfiguration> configurations;
    protected SMTPConfiguration selectedSmtpConfiguration;

    public void create() {
        SMTPConfiguration smtpConfiguration = new SMTPConfiguration();
        smtpConfiguration.setName(name);
        smtpConfiguration.setAddress(address);
        smtpConfiguration.setPort(port);
        smtpConfiguration.setUserName(userName);
        smtpConfiguration.setPassword(password);

        try {
            smtpConfigurationDao.save(smtpConfiguration);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success", "Created new configuration named : " + name ));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Fatal", "There was a problem during creating new smtp configuration."));
        }

    }

    public void update() {
        smtpConfigurationDao.save(selectedSmtpConfiguration);
    }

    @Transactional
    public void setAsActive() {
        if (selectedSmtpConfiguration.getActive()) {
            deactivateCurrent();
        }
        smtpConfigurationDao.save(selectedSmtpConfiguration);

    }

    private void deactivateCurrent() {
        SMTPConfiguration currentActive = smtpConfigurationDao.getActive();
        if (currentActive != null) {
            currentActive.setActive(Boolean.FALSE);
            smtpConfigurationDao.save(currentActive);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SMTPConfigurationDao getSmtpConfigurationDao() {
        return smtpConfigurationDao;
    }

    public void setSmtpConfigurationDao(final SMTPConfigurationDao smtpConfigurationDao) {
        this.smtpConfigurationDao = smtpConfigurationDao;
    }

    public List<SMTPConfiguration> getConfigurations() {
        return configurations = Lists.newArrayList(smtpConfigurationDao.findAll());
    }

    public void setConfigurations(final List<SMTPConfiguration> configurations) {
        this.configurations = configurations;
    }

    public SMTPConfiguration getSelectedSmtpConfiguration() {
        return selectedSmtpConfiguration;
    }

    public void setSelectedSmtpConfiguration(final SMTPConfiguration selectedSmtpConfiguration) {
        this.selectedSmtpConfiguration = selectedSmtpConfiguration;
    }
}
