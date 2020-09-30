package ru.carSale.model.car;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "config")
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "generation_id", foreignKey = @ForeignKey(name = "GENERATION_ID_FK"))
    private Generation generation = new Generation();

    @ManyToOne
    @JoinColumn(name = "body_id", foreignKey = @ForeignKey(name = "BODY_ID_FK"))
    private Body body;

    @ManyToOne
    @JoinColumn(name = "engine_id", foreignKey = @ForeignKey(name = "ENGINE_ID_FK"))
    private Engine engine;

    @ManyToOne
    @JoinColumn(name = "transmission_id", foreignKey = @ForeignKey(name = "TRANSMISSION_ID_FK"))
    private Transmission transmission;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Generation getGeneration() {
        return generation;
    }

    public void setGeneration(Generation generation) {
        this.generation = generation;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    public static Config of(Generation generation, Body body, Engine engine, Transmission transmission) {
        Config config = new Config();
        config.generation = generation;
        config.body = body;
        config.engine = engine;
        config.transmission = transmission;
        return config;
    }

    public Config forJson() {
        Config config = Config.of(this.generation.lazy(), this.body, this.engine, this.transmission);
        config.setId(this.id);
        return config;
    }

    public Config lazy() {
        return forJson();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(generation, config.generation) &&
                Objects.equals(body, config.body) &&
                Objects.equals(engine, config.engine) &&
                Objects.equals(transmission, config.transmission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generation, body, engine, transmission);
    }
}
