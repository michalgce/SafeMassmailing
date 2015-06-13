package pl.polsl.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(unique = true)
    protected String name;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name="SUBSCRIBER_GROUP",
            joinColumns={@JoinColumn(name="SUBSCRIBER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="GROUP_ID", referencedColumnName="ID")})
    protected List<Subscriber> subscriberList;

    public Groups() {
    }

    public Groups(final String groupName) {
        name = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public List<Subscriber> getSubscriberList() {
        return subscriberList;
    }

    public void setSubscriberList(final List<Subscriber> subscriberList) {
        this.subscriberList = subscriberList;
    }
}
