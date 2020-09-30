package ru.carSale.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "password")
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<Advert> adverts = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Set<Advert> getAdverts() {
        return adverts;
    }

    public void addAdvert(Advert advert) {
        this.adverts.add(advert);
    }

    public Customer forJson() {
        Customer customer = new Customer();
        customer.id = this.id;
        customer.name = this.name;
        customer.login = this.login;
        customer.password = "";
        customer.phone = this.phone;
        this.adverts.stream().forEach(advert -> customer.addAdvert(advert.lazy()));
        return customer;
    }

    public Customer lazy() {
        Customer customer = new Customer();
        customer.id = this.id;
        customer.name = this.name;
        customer.login = this.login;
        customer.password = "";
        customer.phone = this.phone;
        return customer;
    }

    public static Customer emptyCustomer() {
        Customer customer = new Customer();
        customer.setName("");
        customer.login = "";
        customer.phone = "";
        customer.password = "";
        return customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id &&
                name.equals(customer.name) &&
                login.equals(customer.login) &&
                phone.equals(customer.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, login, phone);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", adverts=" + adverts +
                '}';
    }
}
