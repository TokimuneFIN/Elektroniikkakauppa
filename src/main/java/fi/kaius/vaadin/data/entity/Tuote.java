package fi.kaius.vaadin.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
public class Tuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nimi;

    @NotBlank
    private String tuotenumero;

    @ManyToOne
    @NotAudited
    private Kategoria kategoria;

    @ManyToMany(fetch = FetchType.EAGER)
    @NotAudited
    private Set<Ominaisuus> ominaisuudet = new HashSet<>(); 

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tuotetiedot_id")
    @NotAudited
    private Tuotetiedot tuotetiedot;

    @NotNull
    @Min(0)
    private Double hinta;

    @NotNull
    @Min(0)
    private Integer varastosaldo;

    @NotNull
    @Min(0)
    private Integer takuuKuukausina;

    @CreatedDate
    @Column(updatable = false)
    private java.time.LocalDate lisaysPaiva = java.time.LocalDate.now();

    public Tuote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNimi() { return nimi; }
    public void setNimi(String nimi) { this.nimi = nimi; }

    public String getTuotenumero() { return tuotenumero; }
    public void setTuotenumero(String tuotenumero) { this.tuotenumero = tuotenumero; }

    public Kategoria getKategoria() { return kategoria; }
    public void setKategoria(Kategoria kategoria) { this.kategoria = kategoria; }

    public Set<Ominaisuus> getOminaisuudet() { return ominaisuudet; }
    public void setOminaisuudet(Set<Ominaisuus> ominaisuudet) { this.ominaisuudet = ominaisuudet; }

    public Tuotetiedot getTuotetiedot() { return tuotetiedot; }
    public void setTuotetiedot(Tuotetiedot tuotetiedot) { this.tuotetiedot = tuotetiedot; }

    public Double getHinta() { return hinta; }
    public void setHinta(Double hinta) { this.hinta = hinta; }

    public Integer getVarastosaldo() { return varastosaldo; }
    public void setVarastosaldo(Integer varastosaldo) { this.varastosaldo = varastosaldo; }

    public Integer getTakuuKuukausina() { return takuuKuukausina; }
    public void setTakuuKuukausina(Integer takuuKuukausina) { this.takuuKuukausina = takuuKuukausina; }

    public java.time.LocalDate getLisaysPaiva() { return lisaysPaiva; }
    public void setLisaysPaiva(java.time.LocalDate lisaysPaiva) { this.lisaysPaiva = lisaysPaiva; }
}