package fi.kaius.vaadin.views.ResetView;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fi.kaius.vaadin.data.entity.User;
import fi.kaius.vaadin.data.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;

@PageTitle("Aseta uusi salasana")
@Route("reset-password")
@AnonymousAllowed
public class ResetPasswordView extends VerticalLayout implements BeforeEnterObserver {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private String token;
    private User user;

    private final PasswordField password = new PasswordField("Uusi salasana");
    private final PasswordField confirmPassword = new PasswordField("Vahvista uusi salasana");
    private final Button saveButton = new Button("Vaihda salasana");

    public ResetPasswordView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> vaihdaSalasana());

        add(new H2("Aseta uusi salasana"), password, confirmPassword, saveButton);
        setVisible(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        token = event.getLocation().getQueryParameters().getParameters().getOrDefault("token", java.util.Collections.emptyList())
                .stream().findFirst().orElse(null);

        if (token == null) {
            event.forwardTo("login");
            return;
        }

        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isPresent() && userOpt.get().getResetTokenExpiry().isAfter(LocalDateTime.now())) {
            this.user = userOpt.get();
            setVisible(true);
        } else {
            Notification.show("Linkki on vanhentunut tai virheellinen.", 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo("login");
        }
    }

    private void vaihdaSalasana() {
        if (password.getValue().isEmpty() || !password.getValue().equals(confirmPassword.getValue())) {
            Notification.show("Salasanat eivät täsmää.");
            return;
        }

        user.setPassword(passwordEncoder.encode(password.getValue()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        Notification.show("Salasana vaihdettu! Voit nyt kirjautua sisään.")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        getUI().ifPresent(ui -> ui.navigate("login"));
    }
}