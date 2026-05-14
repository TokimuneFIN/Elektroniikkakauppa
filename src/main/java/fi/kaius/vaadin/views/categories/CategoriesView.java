package fi.kaius.vaadin.views.categories;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import fi.kaius.vaadin.data.repository.KategoriaRepository;
import fi.kaius.vaadin.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Kategoriat")
@Route(value = "categories/:kategoriaID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "SUPER"})
@Menu(order = 3, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@Uses(Icon.class)
public class CategoriesView extends Div implements BeforeEnterObserver {

    private final String KATEGORIA_ID = "kategoriaID";
    private final String KATEGORIA_EDIT_ROUTE_TEMPLATE = "categories/%s/edit";

    private final Grid<Kategoria> grid = new Grid<>(Kategoria.class, false);

    private TextField nimi;
    private TextField kuvaus;
    private NumberField alvProsentti;
    private TextField hyllySijainti;
    private IntegerField palautusAikaPaivina;

    private final Button cancel = new Button("Peruuta");
    private final Button save = new Button("Tallenna");
    private final Button delete = new Button("Poista");

    private final BeanValidationBinder<Kategoria> binder;

    private Kategoria kategoria;
    private final KategoriaRepository kategoriaRepository;

    public CategoriesView(KategoriaRepository kategoriaRepository) {
        this.kategoriaRepository = kategoriaRepository;
        addClassNames("categories-view");

        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn(Kategoria::getNimi).setHeader("Nimi").setAutoWidth(true);
        grid.addColumn(Kategoria::getKuvaus).setHeader("Kuvaus").setAutoWidth(true);
        grid.addColumn(Kategoria::getAlvProsentti).setHeader("ALV %").setAutoWidth(true);
        grid.addColumn(Kategoria::getPalautusAikaPaivina).setHeader("Palautusaika (päivää)").setAutoWidth(true);
        grid.addColumn(Kategoria::getHyllySijainti).setHeader("Hyllysijainti").setAutoWidth(true);
        
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);


        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(KATEGORIA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CategoriesView.class);
            }
        });

        binder = new BeanValidationBinder<>(Kategoria.class);

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        delete.addClickListener(e -> {
            if (this.kategoria != null && this.kategoria.getId() != null) {
                kategoriaRepository.delete(this.kategoria);
                clearForm();
                refreshGrid();
                Notification.show("Kategoria poistettu");
                UI.getCurrent().navigate(CategoriesView.class);
            }
        });

        save.addClickListener(e -> {
            try {
                if (this.kategoria == null) {
                    this.kategoria = new Kategoria();
                }
                binder.writeBean(this.kategoria);
                kategoriaRepository.save(this.kategoria);
                clearForm();
                refreshGrid();
                Notification.show("Tiedot päivitetty");
                UI.getCurrent().navigate(CategoriesView.class);
            } catch (ValidationException validationException) {
                Notification.show("Päivitys epäonnistui. Tarkista kaikki kentät ja yritä uudestaan.");
            }
        });
        
        refreshGrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> kategoriaId = event.getRouteParameters().get(KATEGORIA_ID).map(Long::parseLong);
        if (kategoriaId.isPresent()) {
            Optional<Kategoria> kategoriaFromBackend = kategoriaRepository.findById(kategoriaId.get());
            if (kategoriaFromBackend.isPresent()) {
                populateForm(kategoriaFromBackend.get());
            } else {
                Notification.show(
                        String.format("Valittua kategoriaa ei löytynyt, ID = %s", kategoriaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(CategoriesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nimi = new TextField("Nimi");
        kuvaus = new TextField("Kuvaus");
        alvProsentti = new NumberField("ALV (%)");
        hyllySijainti = new TextField("Hyllysijainti");
        palautusAikaPaivina = new IntegerField("Palautusaika (päivää)");
        
        formLayout.add(nimi, kuvaus, alvProsentti, hyllySijainti, palautusAikaPaivina);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.setItems(kategoriaRepository.findAll());
    }

    private void clearForm() {
        populateForm(null);
        delete.setVisible(false);
    }

    private void populateForm(Kategoria value) {
        this.kategoria = value;
        binder.readBean(this.kategoria);
        delete.setVisible(value != null && value.getId() != null);
    }
}