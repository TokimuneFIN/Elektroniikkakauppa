package fi.kaius.vaadin.views.products;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fi.kaius.vaadin.data.entity.Kategoria;
import fi.kaius.vaadin.data.entity.Ominaisuus;
import fi.kaius.vaadin.data.entity.Tuote;
import fi.kaius.vaadin.data.entity.Tuotetiedot;
import fi.kaius.vaadin.data.repository.KategoriaRepository;
import fi.kaius.vaadin.data.repository.OminaisuusRepository;
import fi.kaius.vaadin.data.repository.TuoteRepository;
import fi.kaius.vaadin.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;
import java.util.stream.Collectors;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Tuotteet")
@Route(value = "products/:tuoteID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "SUPER"})
@Menu(order = 1, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@Uses(Icon.class)
public class ProductsView extends Div implements BeforeEnterObserver {

    private final String TUOTE_ID = "tuoteID";
    private final String TUOTE_EDIT_ROUTE_TEMPLATE = "products/%s/edit";

    private final Grid<Tuote> grid = new Grid<>(Tuote.class, false);

    private TextField nimi;
    private TextField tuotenumero;
    private ComboBox<Kategoria> kategoria;
    private MultiSelectComboBox<Ominaisuus> ominaisuudet;
    private TextField valmistaja;
    private TextField valmistusmaa;
    private NumberField painoKg;
    private NumberField hinta;
    private IntegerField varastosaldo;
    private IntegerField takuuKuukausina;

    private final Button cancel = new Button("Peruuta");
    private final Button save = new Button("Tallenna");
    private final Button delete = new Button("Poista");

    private final BeanValidationBinder<Tuote> binder;
    private Tuote tuote;
    private final TuoteRepository tuoteRepository;

    public ProductsView(TuoteRepository tuoteRepository, KategoriaRepository kategoriaRepo, OminaisuusRepository ominaisuusRepo) {
        this.tuoteRepository = tuoteRepository;
        addClassNames("products-view");

        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout, kategoriaRepo, ominaisuusRepo);
        add(splitLayout);

        grid.addColumn(Tuote::getNimi).setHeader("Nimi").setAutoWidth(true);
        grid.addColumn(Tuote::getTuotenumero).setHeader("Tuotenumero").setAutoWidth(true);
        grid.addColumn(t -> t.getKategoria() != null ? t.getKategoria().getNimi() : "-").setHeader("Kategoria").setAutoWidth(true);
        
        grid.addColumn(t -> t.getOminaisuudet() != null && !t.getOminaisuudet().isEmpty() 
                ? t.getOminaisuudet().stream().map(Ominaisuus::getNimi).collect(Collectors.joining(", ")) 
                : "-").setHeader("Ominaisuudet").setAutoWidth(true);
        
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TUOTE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ProductsView.class);
            }
        });

        binder = new BeanValidationBinder<>(Tuote.class);

        binder.forField(valmistaja).bind(
            t -> (t.getTuotetiedot() != null) ? t.getTuotetiedot().getValmistaja() : "",
            (t, v) -> { if(t.getTuotetiedot() != null) t.getTuotetiedot().setValmistaja(v); }
        );
        binder.forField(valmistusmaa).bind(
            t -> (t.getTuotetiedot() != null) ? t.getTuotetiedot().getValmistusmaa() : "",
            (t, v) -> { if(t.getTuotetiedot() != null) t.getTuotetiedot().setValmistusmaa(v); }
        );
        binder.forField(painoKg).bind(
            t -> (t.getTuotetiedot() != null && t.getTuotetiedot().getPainoKg() != null) ? t.getTuotetiedot().getPainoKg() : 0.0,
            (t, v) -> { if(t.getTuotetiedot() != null) t.getTuotetiedot().setPainoKg(v); }
        );

        binder.bindInstanceFields(this);

        save.addClickListener(e -> tallenna());
        delete.addClickListener(e -> poista());
        
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        refreshGrid();
    }

    private void tallenna() {
        try {
            if (this.tuote == null) {
                this.tuote = new Tuote();
            }
            if (this.tuote.getTuotetiedot() == null) {
                Tuotetiedot tiedot = new Tuotetiedot();
                tiedot.setTuote(this.tuote);
                this.tuote.setTuotetiedot(tiedot);
            }
            
            binder.writeBean(this.tuote);
            tuoteRepository.save(this.tuote);
            clearForm();
            refreshGrid();
            Notification.show("Tuote tallennettu", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(ProductsView.class);
        } catch (ValidationException e) {
            Notification.show("Tarkista lomakkeen virheet", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void poista() {
        if (this.tuote != null && this.tuote.getId() != null) {
            tuoteRepository.delete(this.tuote);
            clearForm();
            refreshGrid();
            Notification.show("Tuote poistettu", 3000, Position.BOTTOM_START);
            UI.getCurrent().navigate(ProductsView.class);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> tuoteId = event.getRouteParameters().get(TUOTE_ID).map(Long::parseLong);
        if (tuoteId.isPresent()) {
            Optional<Tuote> tuoteFromBackend = tuoteRepository.findById(tuoteId.get());
            if (tuoteFromBackend.isPresent()) {
                populateForm(tuoteFromBackend.get());
            } else {
                refreshGrid();
                event.forwardTo(ProductsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout, KategoriaRepository kategoriaRepo, OminaisuusRepository ominaisuusRepo) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nimi = new TextField("Nimi");
        tuotenumero = new TextField("Tuotenumero");
        
        kategoria = new ComboBox<>("Kategoria");
        kategoria.setItems(kategoriaRepo.findAll());
        kategoria.setItemLabelGenerator(Kategoria::getNimi);
        
        ominaisuudet = new MultiSelectComboBox<>("Ominaisuudet");
        ominaisuudet.setItems(ominaisuusRepo.findAll());
        ominaisuudet.setItemLabelGenerator(Ominaisuus::getNimi);

        valmistaja = new TextField("Valmistaja");
        valmistusmaa = new TextField("Valmistusmaa");
        painoKg = new NumberField("Paino (kg)");
        hinta = new NumberField("Hinta (€)");
        varastosaldo = new IntegerField("Varastosaldo");
        takuuKuukausina = new IntegerField("Takuu (kk)");

        formLayout.add(nimi, tuotenumero, kategoria, ominaisuudet, valmistaja, valmistusmaa, painoKg, hinta, varastosaldo, takuuKuukausina);
        editorDiv.add(formLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.setItems(tuoteRepository.findAll());
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Tuote value) {
        this.tuote = value;
        if (this.tuote != null && this.tuote.getTuotetiedot() == null) {
            Tuotetiedot tt = new Tuotetiedot();
            tt.setTuote(this.tuote);
            this.tuote.setTuotetiedot(tt);
        }
        binder.readBean(this.tuote);
        delete.setVisible(value != null && value.getId() != null);
    }
}