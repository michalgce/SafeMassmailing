package pl.polsl.service;

import com.google.common.collect.Lists;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.polsl.dao.GroupDao;
import pl.polsl.dao.SubscriberDao;
import pl.polsl.entity.Groups;
import pl.polsl.entity.Subscriber;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@ManagedBean
public class GroupService {

    @Autowired
    protected GroupDao groupDao;

    @Autowired
    protected SubscriberDao subscriberDao;

    protected List<Groups> groups;

    protected Groups selectedGroups;

    protected String groupName;

    protected List<Subscriber> groupSubscribers;
    protected List<Subscriber> subscribersOutsideGroup;
    protected DualListModel<Subscriber> subscriberDualListModel;

    @PostConstruct
    public void fetchAvailableGroups() {
        groups = Lists.newArrayList(groupDao.findAll());
    }

    public void createNewGroup() {
        if (isEmpty(groupName)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", "Enter new group name."));
            return;
        }

        try {
            Groups groupsFromDB = new Groups(groupName);
            groupDao.save(groupsFromDB);
            fetchAvailableGroups();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "New group has been created."));
        } catch (DataIntegrityViolationException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal",
                    "Probably entered group name has already exists. Enter new unique group name."));
        }

    }

    @Transactional
    public void choiceGroup() {
        groupSubscribers = subscriberDao.listGroupSubscribers(selectedGroups.getId());
        subscribersOutsideGroup = retrieveSubscriberOutsideGroup();

        if (subscribersOutsideGroup != null && groupSubscribers != null) {
            prepareDualList();
        }

        RequestContext.getCurrentInstance().update("tabView:groupAssigner");
    }

    protected List<Subscriber> retrieveSubscriberOutsideGroup() {
        List<Subscriber> allSubscribers = Lists.newArrayList(subscriberDao.findAll());
        groupSubscribers.forEach(allSubscribers::remove);

        return allSubscribers;
    }

    protected void prepareDualList() {
        subscriberDualListModel = new DualListModel<>(subscribersOutsideGroup, groupSubscribers);
    }

    @Transactional
    public void saveTransfer() {
        try {
            removeFromGroup(subscriberDualListModel.getSource());
            addToGroup(subscriberDualListModel.getTarget());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success", "Subscriber has been assigned to : " + selectedGroups.getName() ));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Fatal", "There was a problem with assign member to group. Please contact with Administrator"));
        }

    }

    protected void addToGroup(final List<Subscriber> target) {
        target.stream().forEach(subscriber -> {
            final Subscriber subscriberMerged = subscriberDao.findOne(subscriber.getId());
            final Optional<Groups> currentGroup = findCurrentGroupForSubscriber(subscriberMerged);

            Boolean isSubscriberPresentInSelectedGroup = currentGroup.isPresent();

            if (!isSubscriberPresentInSelectedGroup) {
                final Groups groupsMerged = groupDao.findOne(selectedGroups.getId());
                groupsMerged.getSubscriberList().add(subscriberMerged);
            }
        });
    }

    protected void removeFromGroup(final List<Subscriber> source) {
        source.stream().forEach(subscriber -> {
            final Subscriber subscriberMerged = subscriberDao.findOne(subscriber.getId());
            final Optional<Groups> currentGroup = findCurrentGroupForSubscriber(subscriberMerged);

            Boolean isSubscriberPresentInSelectedGroup = currentGroup.isPresent();

            if (isSubscriberPresentInSelectedGroup) {
                final Groups groupsMerged = groupDao.findOne(selectedGroups.getId());
                groupsMerged.getSubscriberList().remove(subscriberMerged);
            }
        });
    }

    protected Optional<Groups> findCurrentGroupForSubscriber(final Subscriber subscriber) {

        return subscriber.getGroupsList().stream()
                .filter(group -> group.getId().equals(selectedGroups.getId())).findFirst();
    }

    public List<Groups> getGroups() {
        return groups;
    }

    public void setGroups(final List<Groups> groups) {
        this.groups = groups;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public Groups getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(final Groups selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public List<Subscriber> getSubscribersOutsideGroup() {
        return subscribersOutsideGroup;
    }

    public void setSubscribersOutsideGroup(final List<Subscriber> subscribersOutsideGroup) {
        this.subscribersOutsideGroup = subscribersOutsideGroup;
    }

    public DualListModel<Subscriber> getSubscriberDualListModel() {
        return subscriberDualListModel;
    }

    public void setSubscriberDualListModel(final DualListModel<Subscriber> subscriberDualListModel) {
        this.subscriberDualListModel = subscriberDualListModel;
    }
}
