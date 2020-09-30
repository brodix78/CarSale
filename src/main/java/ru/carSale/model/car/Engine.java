package ru.carSale.model.car;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "engine")
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "h_power", nullable = false)
    private double hPower;

    @Column(name = "value", nullable = false)
    private double value;

    @ManyToOne
    @JoinColumn(name = "fuel_id", foreignKey = @ForeignKey(name = "FUEL_ID_FK"))
    private Fuel fuel;

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

    public double getHPower() {
        return hPower;
    }

    public void setHPower(double hPower) {
        this.hPower = hPower;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Fuel getFuel() {
        return fuel;
    }

    public void setFuel(Fuel fuel) {
        this.fuel = fuel;
    }

    public static Engine of(String name, Fuel fuel, double value, double hPower) {
        Engine engine = new Engine();
        engine.name = name;
        engine.fuel = fuel;
        engine.value = value;
        engine.hPower = hPower;
        return engine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Engine engine = (Engine) o;
        return Double.compare(engine.hPower, hPower) == 0 &&
                Double.compare(engine.value, value) == 0 &&
                Objects.equals(name, engine.name) &&
                Objects.equals(fuel, engine.fuel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, hPower, value, fuel);
    }
}
