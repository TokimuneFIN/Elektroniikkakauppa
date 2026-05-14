package fi.kaius.vaadin.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
public class Tuotetiedot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Valmistaja on pakollinen")
    private String valmistaja;

    @NotBlank(message = "Valmistusmaa on pakollinen")
    private String valmistusmaa;

    @Positive(message = "Painon on oltava positiivinen luku")
    private Double painoKg;

    @OneToOne(mappedBy = "tuotetiedot")
    private Tuote tuote;

    public Tuotetiedot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getValmistaja() { return valmistaja; }
    public void setValmistaja(String valmistaja) { this.valmistaja = valmistaja; }
    public String getValmistusmaa() { return valmistusmaa; }
    public void setValmistusmaa(String valmistusmaa) { this.valmistusmaa = valmistusmaa; }
    public Double getPainoKg() { return painoKg; }
    public void setPainoKg(Double painoKg) { this.painoKg = painoKg; }
    public Tuote getTuote() { return tuote; }
    public void setTuote(Tuote tuote) { this.tuote = tuote; }
}