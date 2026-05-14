package fi.kaius.vaadin.views.ForgotView;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fi.kaius.vaadin.data.repository.UserRepository;
import fi.kaius.vaadin.service.EmailService;
import java.time.LocalDateTime;
import java.util.UUID;

@PageTitle("Unohtunut salasana")
@Route("forgot-password")
@AnonymousAllowed
@PreserveOnRefresh
public class ForgotPasswordView extends VerticalLayout {

    public ForgotPasswordView(UserRepository userRepository, EmailService emailService) {
        setVisible(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        H2 title = new H2("Unohtuiko salasana?");
        Span instruction = new Span("Lähetämme sähköpostiisi linkin, jolla voit asettaa uuden salasanan.");
        
        TextField emailField = new TextField("Sähköpostiosoitteesi");
        emailField.setPlaceholder("");
        emailField.setWidth("300px");
        emailField.setRequired(true);
        
        Button sendButton = new Button("Lähetä linkki", e -> {
            userRepository.findByEmail(emailField.getValue()).ifPresentOrElse(user -> {
                String token = UUID.randomUUID().toString();
                user.setResetToken(token);
                user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
                userRepository.save(user);

                emailService.lähetäSalasananPalautus(user.getEmail(), token);
                Notification.show("Linkki lähetetty! Tarkista sähköpostisi.", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }, () -> {
                Notification.show("Sähköpostiosoitetta ei löytynyt.", 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            });
        });

        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        add(title, instruction, emailField, sendButton);
    }
}