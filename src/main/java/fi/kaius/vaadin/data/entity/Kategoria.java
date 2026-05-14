package fi.kaius.vaadin.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
public class Kategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nimi on pakollinen")
    @Size(min = 2, max = 50, message = "Nimen pitää olla 2-50 merkkiä")
    private String nimi;

    @NotBlank(message = "Kuvaus on pakollinen")
    private String kuvaus;

    @NotNull(message = "ALV on pakollinen")
    @Min(value = 0, message = "ALV ei voi olla alle 0")
    @Max(value = 100, message = "ALV ei voi olla yli 100")
    private Double alvProsentti;

    @NotBlank(message = "Hyllysijainti on pakollinen")
    private String hyllySijainti;

    @NotNull(message = "Palautusaika on pakollinen")
    @Min(value = 0, message = "Palautusaika ei voi olla negatiivinen")
    private Integer palautusAikaPaivina;

    @OneToMany(mappedBy = "kategoria")
    private List<Tuote> tuotteet;

    public Kategoria() {}

    public Kategoria(String nimi, String kuvaus, Double alvProsentti, String hyllySijainti, Integer palautusAikaPaivina) {
        this.nimi = nimi;
        this.kuvaus = kuvaus;
        this.alvProsentti = alvProsentti;
        this.hyllySijainti = hyllySijainti;
        this.palautusAikaPaivina = palautusAikaPaivina;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNimi() { return nimi; }
    public void setNimi(String nimi) { this.nimi = nimi; }

    public String getKuvaus() { return kuvaus; }
    public void setKuvaus(String kuvaus) { this.kuvaus = kuvaus; }

    public Double getAlvProsentti() { return alvProsentti; }
    public void setAlvProsentti(Double alvProsentti) { this.alvProsentti = alvProsentti; }

    public String getHyllySijainti() { return hyllySijainti; }
    public void setHyllySijainti(String hyllySijainti) { this.hyllySijainti = hyllySijainti; }

    public Integer getPalautusAikaPaivina() { return palautusAikaPaivina; }
    public void setPalautusAikaPaivina(Integer palautusAikaPaivina) { this.palautusAikaPaivina = palautusAikaPaivina; }

    public List<Tuote> getTuotteet() { return tuotteet; }
    public void setTuotteet(List<Tuote> tuotteet) { this.tuotteet = tuotteet; }
}