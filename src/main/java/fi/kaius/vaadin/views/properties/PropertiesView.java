package fi.kaius.vaadin.views.properties;

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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fi.kaius.vaadin.data.entity.Ominaisuus;
import fi.kaius.vaadin.data.repository.OminaisuusRepository;
import fi.kaius.vaadin.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Ominaisuudet")
@Route(value = "properties/:propertyID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Menu(order = 5, icon = LineAwesomeIconUrl.TAGS_SOLID)
@Uses(Icon.class)
public class PropertiesView extends Div implements BeforeEnterObserver {

    private final String PROPERTY_ID = "propertyID";
    private final String PROPERTY_EDIT_ROUTE_TEMPLATE = "properties/%s/edit";

    private final Grid<Ominaisuus> grid = new Grid<>(Ominaisuus.class, false);

    private TextField nimi;

    private final Button cancel = new Button("Peruuta");
    private final Button save = new Button("Tallenna");
    private final Button delete = new Button("Poista");

    private final BeanValidationBinder<Ominaisuus> binder;

    private Ominaisuus ominaisuus;
    private final OminaisuusRepository ominaisuusRepository;

    public PropertiesView(OminaisuusRepository ominaisuusRepository) {
        this.ominaisuusRepository = ominaisuusRepository;
        addClassNames("properties-view");

        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addColumn(Ominaisuus::getNimi).setHeader("Ominaisuuden nimi").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PROPERTY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PropertiesView.class);
            }
        });

        binder = new BeanValidationBinder<>(Ominaisuus.class);
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        delete.addClickListener(e -> {
            if (this.ominaisuus != null && this.ominaisuus.getId() != null) {
                ominaisuusRepository.delete(this.ominaisuus);
                clearForm();
                refreshGrid();
                Notification.show("Ominaisuus poistettu");
                UI.getCurrent().navigate(PropertiesView.class);
            }
        });

        save.addClickListener(e -> {
            try {
                if (this.ominaisuus == null) {
                    this.ominaisuus = new Ominaisuus();
                }
                binder.writeBean(this.ominaisuus);
                ominaisuusRepository.save(this.ominaisuus);
                clearForm();
                refreshGrid();
                Notification.show("Tiedot tallennettu");
                UI.getCurrent().navigate(PropertiesView.class);
            } catch (ValidationException validationException) {
                Notification.show("Nimi on pakollinen (2-50 merkkiä).");
            }
        });
        
        refreshGrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> propertyId = event.getRouteParameters().get(PROPERTY_ID).map(Long::parseLong);
        if (propertyId.isPresent()) {
            Optional<Ominaisuus> fromBackend = ominaisuusRepository.findById(propertyId.get());
            if (fromBackend.isPresent()) {
                populateForm(fromBackend.get());
            } else {
                Notification.show(
                        String.format("Ominaisuutta ei löytynyt, ID = %s", propertyId.get()), 3000,
                        Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(PropertiesView.class);
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
        nimi = new TextField("Ominaisuuden nimi (esim. Vedenkestävä)");
        
        formLayout.add(nimi);

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
        grid.setItems(ominaisuusRepository.findAll());
    }

    private void clearForm() {
        populateForm(null);
        delete.setVisible(false);
    }

    private void populateForm(Ominaisuus value) {
        this.ominaisuus = value;
        binder.readBean(this.ominaisuus);
        delete.setVisible(value != null && value.getId() != null);
    }
}