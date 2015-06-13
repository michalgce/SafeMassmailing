package pl.polsl.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Subscriber {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String firstName;

    protected String lastName;

    @Column(unique = true)
    protected String mail;

    @ManyToMany(mappedBy = "subscriberList", cascade = CascadeType.ALL)
    protected List<Groups> groupsList;

    public Subscriber() {
    }

    public Subscriber(final String firstName, final String lastName, final String mail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public List<Groups> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(final List<Groups> groupsList) {
        this.groupsList = groupsList;
    }
}
