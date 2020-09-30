package ru.carSale.model.car;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "fuel")
public class Fuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type", nullable = false)
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Fuel of(String type) {
        Fuel fuel = new Fuel();
        fuel.type = type;
        return fuel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fuel fuel = (Fuel) o;
        return Objects.equals(type, fuel.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
