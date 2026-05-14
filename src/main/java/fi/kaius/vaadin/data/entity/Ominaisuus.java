package fi.kaius.vaadin.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
public class Ominaisuus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nimi on pakollinen")
    @Size(min = 2, max = 50, message = "Nimen pitää olla 2-50 merkkiä")
    private String nimi;

    @ManyToMany(mappedBy = "ominaisuudet")
    private List<Tuote> tuotteet;

    public Ominaisuus() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNimi() { return nimi; }
    public void setNimi(String nimi) { this.nimi = nimi; }

    public List<Tuote> getTuotteet() { return tuotteet; }
    public void setTuotteet(List<Tuote> tuotteet) { this.tuotteet = tuotteet; }
}