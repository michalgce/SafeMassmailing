package pl.polsl.entity;

import javax.persistence.*;

@Entity(name = "smtp_configuration")
public class SMTPConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(unique = true)
    protected String name;

    protected String address;

    protected Long port;

    @Column(name = "user_name")
    protected String userName;

    protected String password;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public Long getPort() {
        return port;
    }

    public void setPort(final Long port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
