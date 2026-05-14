package fi.kaius.vaadin.views.login;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fi.kaius.vaadin.views.registration.RegistrationView;
import fi.kaius.vaadin.views.ForgotView.ForgotPasswordView;

@PageTitle("Login")
@Route(value = "login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");
        
        login.setForgotPasswordButtonVisible(false);

        RouterLink registrationLink = new RouterLink("Ei tunnusta? Rekisteröidy tästä", RegistrationView.class);
        
        RouterLink forgotPasswordLink = new RouterLink("Unohtuiko salasana?", ForgotPasswordView.class);

        Anchor googleLogin = new Anchor("/oauth2/authorization/google", "Kirjaudu Googlella");
        googleLogin.getElement().setAttribute("router-ignore", true);
        googleLogin.getElement().setAttribute("theme", "button");

        Anchor githubLogin = new Anchor("/oauth2/authorization/github", "Kirjaudu GitHubilla");
        githubLogin.getElement().setAttribute("router-ignore", true);
        githubLogin.getElement().setAttribute("theme", "button");

        HorizontalLayout oauthButtons = new HorizontalLayout(googleLogin, githubLogin);
        oauthButtons.setPadding(true);

        add(new H1("Elektroniikkakauppa"), login, registrationLink, forgotPasswordLink, new Span("tai"), oauthButtons);
    }
}