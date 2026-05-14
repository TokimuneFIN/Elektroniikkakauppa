package fi.kaius.vaadin.views.home;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import fi.kaius.vaadin.views.MainLayout;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Etusivu")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
public class HomeView extends VerticalLayout {

    public HomeView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        H1 otsikko = new H1("Elektroniikkakaupan Varastohallinta");
        Paragraph teksti = new Paragraph("Tervetuloa järjestelmään! Valitse vasemman reunan valikosta haluamasi toiminto.");
        Paragraph teksti2 = new Paragraph("Voit hallinnoida tuotteita, kategorioita tai hakea tuotteita.");

        add(otsikko, teksti, teksti2);
    }
}