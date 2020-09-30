package ru.carSale.model.car;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "generation")
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Generation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "model_id", foreignKey = @ForeignKey(name = "MODEL_ID_FK"))
    private Model model;

    @Column(name = "start_year")
    private int startYear;

    @Column(name = "end_year")
    private int endYear;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "generation")
    private Set<Config> configs = new HashSet<>();

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

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public Set<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(Set<Config> configs) {
        this.configs = configs;
    }

    public void addConfig(Config config) {
        this.configs.add(config);
    }

    public static Generation of(String name, Model model, int startYear, int endYear) {
        Generation generation = new Generation();
        generation.name = name;
        generation.model = model;
        generation.startYear = startYear;
        generation.endYear = endYear;
        return generation;
    }

    public Generation forJson() {
        Generation generation = Generation.of(this.name, this.model.lazy(), this.startYear, this.endYear);
        generation.setId(this.id);
        this.configs.stream().forEach(config -> generation.addConfig(config.lazy()));
        return generation;
    }

    public Generation lazy() {
        Generation generation = Generation.of(this.name, this.model.lazy(), this.startYear, this.endYear);
        generation.setId(this.id);
        return generation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Generation that = (Generation) o;
        return startYear == that.startYear &&
                endYear == that.endYear &&
                Objects.equals(name, that.name) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, model, startYear, endYear);
    }
}
