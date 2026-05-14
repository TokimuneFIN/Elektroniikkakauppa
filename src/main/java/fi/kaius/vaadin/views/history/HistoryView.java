package fi.kaius.vaadin.views.history;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fi.kaius.vaadin.data.entity.Tuote;
import fi.kaius.vaadin.service.HistoryService;
import fi.kaius.vaadin.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Muokkaushistoria")
@Route(value = "history", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Menu(order = 8, icon = LineAwesomeIconUrl.HISTORY_SOLID)
public class HistoryView extends VerticalLayout {

    private final HistoryService historyService;
    private final Grid<HistoryRecord> grid = new Grid<>(HistoryRecord.class, false);

    public HistoryView(HistoryService historyService) {
        this.historyService = historyService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();

        H2 title = new H2("Tuotteiden muokkaushistoria");
        add(title);

        grid.addColumn(HistoryRecord::getId).setHeader("Tuote ID").setAutoWidth(true);
        grid.addColumn(HistoryRecord::getNimi).setHeader("Nimi").setAutoWidth(true);
        grid.addColumn(HistoryRecord::getHinta).setHeader("Hinta (€)").setAutoWidth(true);
        grid.addColumn(HistoryRecord::getVarastosaldo).setHeader("Varasto").setAutoWidth(true);
        grid.addColumn(HistoryRecord::getMuutostyyppi).setHeader("Toiminto").setAutoWidth(true);
        grid.addColumn(HistoryRecord::getRevisionNumber).setHeader("Revisio ID").setAutoWidth(true);
        
        grid.setSizeFull();
        add(grid);

        lataaMuutoshistoria();
    }

    private void lataaMuutoshistoria() {
        List<Object[]> historiaData = historyService.haeKaikkiTuoteMuutokset();

        List<HistoryRecord> records = historiaData.stream()
                .map(row -> {
                    Tuote tuote = (Tuote) row[0];
                    Object revisionInfo = row[2];
                    String muutostyyppi = row[1].toString();

                    return new HistoryRecord(
                            tuote.getId(),
                            tuote.getNimi(),
                            tuote.getHinta(),
                            tuote.getVarastosaldo(),
                            muutostyyppi,
                            revisionInfo.toString()
                    );
                })
                .toList();

        grid.setItems(records);
    }

    public static class HistoryRecord {
        private Long id;
        private String nimi;
        private Double hinta;
        private Integer varastosaldo;
        private String muutostyyppi;
        private String revisionNumber;

        public HistoryRecord(Long id, String nimi, Double hinta, Integer varastosaldo, String muutostyyppi, String revisionNumber) {
            this.id = id;
            this.nimi = nimi;
            this.hinta = hinta;
            this.varastosaldo = varastosaldo;
            this.muutostyyppi = muutostyyppi;
            this.revisionNumber = revisionNumber;
        }

        public Long getId() { return id; }
        public String getNimi() { return nimi; }
        public Double getHinta() { return hinta; }
        public Integer getVarastosaldo() { return varastosaldo; }
        public String getMuutostyyppi() { return muutostyyppi; }
        public String getRevisionNumber() { return revisionNumber; }
    }
}