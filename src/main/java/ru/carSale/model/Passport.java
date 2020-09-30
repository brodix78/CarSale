package ru.carSale.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "passport")
public class Passport {

    @Id
    @JoinColumn(name = "login", foreignKey = @ForeignKey(name = "CUSTOMER_ID_FK"))
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    public static Passport of (String login, String password) {
        Passport passport = new Passport();
        passport.password = password;
        passport.login = login;
        return passport;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passport passport = (Passport) o;
        return Objects.equals(login, passport.login) &&
                Objects.equals(password, passport.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }
}
