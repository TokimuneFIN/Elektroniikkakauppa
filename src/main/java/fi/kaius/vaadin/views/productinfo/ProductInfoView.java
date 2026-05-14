package fi.kaius.vaadin.views.productinfo;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fi.kaius.vaadin.data.entity.Tuotetiedot;
import fi.kaius.vaadin.data.repository.TuotetiedotRepository;
import fi.kaius.vaadin.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.format.DateTimeFormatter;

@PageTitle("Tarkat Tuotetiedot")
@Route(value = "productinfo", layout = MainLayout.class)
@PermitAll
@Menu(order = 2, icon = LineAwesomeIconUrl.INFO_CIRCLE_SOLID)
public class ProductInfoView extends VerticalLayout {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ProductInfoView(TuotetiedotRepository repository) {
        setSizeFull();

        H2 otsikko = new H2("Tekniset tuotetiedot");
        
        Grid<Tuotetiedot> grid = new Grid<>(Tuotetiedot.class, false);
        
        grid.addColumn(tt -> tt.getTuote() != null ? tt.getTuote().getNimi() : "-")
            .setHeader("Tuote")
            .setSortable(true)
            .setAutoWidth(true);

        grid.addColumn(tt -> tt.getTuote() != null ? tt.getTuote().getTuotenumero() : "-")
            .setHeader("Tuotenumero / EAN")
            .setSortable(true)
            .setAutoWidth(true);
            
        grid.addColumn(Tuotetiedot::getValmistaja)
            .setHeader("Valmistaja")
            .setSortable(true)
            .setAutoWidth(true);
            
        grid.addColumn(Tuotetiedot::getValmistusmaa)
            .setHeader("Valmistusmaa")
            .setAutoWidth(true);
            
        grid.addColumn(tt -> tt.getPainoKg() != null ? tt.getPainoKg() + " kg" : "-")
            .setHeader("Paino")
            .setAutoWidth(true);

        grid.addColumn(tt -> tt.getTuote() != null ? tt.getTuote().getVarastosaldo() + " kpl" : "0 kpl")
            .setHeader("Varastosaldo")
            .setSortable(true)
            .setAutoWidth(true);

        grid.addColumn(tt -> tt.getTuote() != null && tt.getTuote().getLisaysPaiva() != null ? 
                tt.getTuote().getLisaysPaiva().format(formatter) : "-")
            .setHeader("Lisätty järjestelmään")
            .setAutoWidth(true);

        grid.setItems(repository.findAll());
        add(otsikko, grid);
    }
}