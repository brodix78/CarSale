package ru.carSale.model;

import ru.carSale.model.car.Config;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "advert")
public class Advert {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "CUSTOMER_ID_FK"))
    private Customer customer;

    @Column(name = "report")
    private String report;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "date", nullable = false)
    private long date;

    @Column(name = "mileage", nullable = false)
    private int mileage;

    @Column(name = "production_year")
    private int prodYear;

    @ManyToOne
    @JoinColumn(name = "config_id", foreignKey = @ForeignKey(name = "CONFIG_ID_FK"), nullable = false)
    private Config config;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Photo> photos = new HashSet<>();

    @Column(name = "sold", nullable = false)
    private boolean sold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public int getProdYear() {
        return prodYear;
    }

    public void setProdYear(int prodYear) {
        this.prodYear = prodYear;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Advert forJson() {
        Advert advert = new Advert();
        advert.id = this.id;
        advert.customer = this.customer.lazy();
        advert.report = this.report;
        advert.price = this.price;
        advert.date = this.date;
        advert.mileage = this.mileage;
        advert.prodYear = this.prodYear;
        advert.config = this.config.lazy();
        advert.photos = this.photos;
        return advert;
    }

    public Advert lazy() {
        return forJson();
    }

    public static Advert ofLazyMap(Map map) {
        Advert advert = new Advert();
        advert.id = (int) map.get("id");
        advert.customer = Customer.emptyCustomer();
        advert.customer.setId((int) ((Map) map.get("customer")).get("id"));
        advert.report = (String) map.get("report");
        advert.price = (int) map.get("price");
        advert.date = (long) map.get("date");
        advert.mileage = (int) map.get("mileage");
        advert.prodYear = (int) map.get("prodYear");
        advert.sold = (boolean) map.get("sold");
        advert.config = new Config();
        advert.config.setId((int) ((Map) map.get("config")).get("id"));
        List photos = (List) map.get("photos");
        for (Object obj : photos) {
            Map phObj = (Map) obj;
            Photo photo = Photo.of((String) phObj.get("file"));
            photo.setId((int) phObj.get("id"));
            advert.addPhoto(photo);
        }
        return advert;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advert advert = (Advert) o;
        return id == advert.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
