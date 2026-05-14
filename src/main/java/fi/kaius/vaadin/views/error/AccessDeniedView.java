package fi.kaius.vaadin.views.error;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AccessDeniedException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;

@PageTitle("Pääsy kielletty")
@Route("access-denied")
@AnonymousAllowed
public class AccessDeniedView extends VerticalLayout implements HasErrorParameter<AccessDeniedException> {

    public AccessDeniedView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        add(new H1("Hups! Pääsy kielletty."));
        add(new Span("Sinulla ei ole tarvittavia oikeuksia tämän sivun katseluun."));
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, com.vaadin.flow.router.ErrorParameter<AccessDeniedException> parameter) {
        return HttpServletResponse.SC_FORBIDDEN;
    }
}