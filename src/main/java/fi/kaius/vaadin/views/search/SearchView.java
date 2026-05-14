package fi.kaius.vaadin.views.search;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fi.kaius.vaadin.views.MainLayout;
import jakarta.annotation.security.PermitAll;

import fi.kaius.vaadin.data.entity.Kategoria;
import fi.kaius.vaadin.data.entity.Tuote;
import fi.kaius.vaadin.data.repository.KategoriaRepository;
import fi.kaius.vaadin.data.repository.TuoteRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Tuotehaku")
@Route(value = "search", layout = MainLayout.class)
@PermitAll
@Menu(order = 4, icon = LineAwesomeIconUrl.SEARCH_SOLID)
public class SearchView extends VerticalLayout {

    private final TuoteRepository tuoteRepository;
    private final Grid<Tuote> grid = new Grid<>(Tuote.class, false);

    private final TextField hakusana = new TextField("Hae nimellä tai tuotenumerolla");
    private final ComboBox<Kategoria> kategoriaFilter = new ComboBox<>("Kategoria");
    private final DatePicker alkuPvm = new DatePicker("Lisätty alkaen");
    private final DatePicker loppuPvm = new DatePicker("Lisätty päättyen");
    private final Button haeBtn = new Button("Suorita haku");

    public SearchView(TuoteRepository tuoteRepository, KategoriaRepository kategoriaRepo) {
        this.tuoteRepository = tuoteRepository;
        setSizeFull();

        kategoriaFilter.setItems(kategoriaRepo.findAll());
        kategoriaFilter.setItemLabelGenerator(Kategoria::getNimi);

        haeBtn.addClickListener(e -> suoritaCriteriaApiHaku());

        HorizontalLayout hakuRivi = new HorizontalLayout(hakusana, kategoriaFilter, alkuPvm, loppuPvm, haeBtn);
        hakuRivi.setDefaultVerticalComponentAlignment(Alignment.END);

        grid.addColumn(Tuote::getNimi).setHeader("Nimi");
        grid.addColumn(Tuote::getTuotenumero).setHeader("Tuotenumero");
        grid.addColumn(t -> t.getKategoria() != null ? t.getKategoria().getNimi() : "-").setHeader("Kategoria");
        grid.addColumn(Tuote::getLisaysPaiva).setHeader("Lisäyspäivä");
        grid.addColumn(Tuote::getHinta).setHeader("Hinta");

        add(hakuRivi, grid);
        
        grid.setItems(tuoteRepository.findAll()); 
    }

    private void suoritaCriteriaApiHaku() {
        Specification<Tuote> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hakusana.getValue() != null && !hakusana.getValue().isBlank()) {
                String pattern = "%" + hakusana.getValue().toLowerCase() + "%";
                Predicate nimiLike = cb.like(cb.lower(root.get("nimi")), pattern);
                Predicate numeroLike = cb.like(cb.lower(root.get("tuotenumero")), pattern);
                predicates.add(cb.or(nimiLike, numeroLike));
            }

            if (kategoriaFilter.getValue() != null) {
                Join<Tuote, Kategoria> kategoriaJoin = root.join("kategoria");
                predicates.add(cb.equal(kategoriaJoin.get("id"), kategoriaFilter.getValue().getId()));
            }

            if (alkuPvm.getValue() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lisaysPaiva"), alkuPvm.getValue()));
            }
            if (loppuPvm.getValue() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lisaysPaiva"), loppuPvm.getValue()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        grid.setItems(tuoteRepository.findAll(spec));
    }
}