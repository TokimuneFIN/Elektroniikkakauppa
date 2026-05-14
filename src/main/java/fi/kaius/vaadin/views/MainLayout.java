package fi.kaius.vaadin.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fi.kaius.vaadin.security.SecurityService;
import fi.kaius.vaadin.views.home.HomeView;
import fi.kaius.vaadin.views.products.ProductsView;
import fi.kaius.vaadin.views.productinfo.ProductInfoView;
import fi.kaius.vaadin.views.categories.CategoriesView;
import fi.kaius.vaadin.views.properties.PropertiesView;
import fi.kaius.vaadin.views.history.HistoryView;
import fi.kaius.vaadin.views.admin.AdminView;
import fi.kaius.vaadin.views.search.SearchView;

public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Button logout = new Button("Kirjaudu ulos", e -> securityService.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout header = new HorizontalLayout(toggle, viewTitle, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(viewTitle);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(true, header);
    }

    private void addDrawerContent() {
        Span appName = new Span("Varastohallinta");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        addToDrawer(new Header(appName), new Scroller(createNavigation()), 
        new Footer(new Paragraph("© 2026 Kaius Kolehmainen")));
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Etusivu", HomeView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Tuotehallinta", ProductsView.class, VaadinIcon.PACKAGE.create()));
        nav.addItem(new SideNavItem("Varastotilanne", ProductInfoView.class, VaadinIcon.INFO_CIRCLE.create()));
        nav.addItem(new SideNavItem("Tuoete Kategoriat", CategoriesView.class, VaadinIcon.ARCHIVE.create()));
        nav.addItem(new SideNavItem("Tuotteiden ominaisuudet", PropertiesView.class, VaadinIcon.TAGS.create()));
        nav.addItem(new SideNavItem("Tuotehaku", SearchView.class, VaadinIcon.SEARCH.create()));
        nav.addItem(new SideNavItem("Tuotteiden Muokkashistoria", HistoryView.class, VaadinIcon.CALENDAR.create()));
        nav.addItem(new SideNavItem("Käyttäjien ylläpito", AdminView.class, VaadinIcon.USER_CHECK.create()));
        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}