package ru.carSale.model.car;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "model")
public class Model implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id", foreignKey = @ForeignKey(name = "BRAND_ID_FK"))
    private Brand brand;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "model")
    private Set<Generation> generations = new HashSet<>();

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

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Set<Generation> getGenerations() {
        return generations;
    }

    public void setGenerations(Set<Generation> generations) {
        this.generations = generations;
    }

    public void addGeneration(Generation generation) {
        this.generations.add(generation);
    }

    public static Model of(String name, Brand brand) {
        Model model = new Model();
        model.name = name;
        model.brand = brand;
        return model;
    }

    public Model forJson() {
        Model model = Model.of(this.name, this.brand.lazy());
        model.setId(this.id);
        this.generations.stream().forEach(generation -> model.addGeneration(generation.lazy()));
        return model;
    }

    public Model lazy() {
        Model model = Model.of(this.name, this.brand.lazy());
        model.setId(this.id);
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(name, model.name) &&
                Objects.equals(brand, model.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, brand);
    }
}
