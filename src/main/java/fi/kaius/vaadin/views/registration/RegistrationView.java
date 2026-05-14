package fi.kaius.vaadin.views.registration;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fi.kaius.vaadin.data.entity.Role;
import fi.kaius.vaadin.data.entity.User;
import fi.kaius.vaadin.data.repository.UserRepository;
import fi.kaius.vaadin.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Set;

@PageTitle("Rekisteröidy")
@Route("registration")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    private byte[] profilePicData;

    public RegistrationView(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setMaxWidth("450px"); 
        formContainer.setWidthFull();
        formContainer.setPadding(true);
        formContainer.addClassNames("bg-base", "border", "border-contrast-10", "shadow-m", "rounded-l");

        var name = new TextField("Nimi");
        var username = new TextField("Käyttäjätunnus");
        var email = new EmailField("Sähköposti");
        var password = new PasswordField("Salasana");

        FormLayout formLayout = new FormLayout(name, username, email, password);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setWidthFull();
        
        upload.addSucceededListener(event -> {
            try {
                profilePicData = buffer.getInputStream().readAllBytes();
                Notification.show("Kuva ladattu!");
            } catch (IOException ex) {
                Notification.show("Virhe kuvan käsittelyssä");
            }
        });

        Button registerButton = new Button("Rekisteröidy", e -> {
            if (userRepository.findByUsername(username.getValue()).isPresent()) {
                Notification n = Notification.show("Käyttäjätunnus '" + username.getValue() + "' on jo varattu!");
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                User newUser = new User();
                newUser.setName(name.getValue());
                newUser.setUsername(username.getValue());
                newUser.setEmail(email.getValue());
                newUser.setPassword(passwordEncoder.encode(password.getValue()));
                newUser.setRoles(Set.of(Role.USER));
                newUser.setProfilePicture(profilePicData);
                
                userRepository.save(newUser);
                
                emailService.lähhetäRekisteröintiSähköposti(email.getValue(), username.getValue());
                emailService.lähhetäYlläpitäjäIlmoitus(username.getValue());
                
                Notification n = Notification.show("Rekisteröinti onnistui! Voit nyt kirjautua.");
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                getUI().ifPresent(ui -> ui.navigate("login"));
            } catch (Exception ex) {
                Notification.show("Virhe tallennuksessa: " + ex.getMessage());
            }
        });

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();

        formContainer.add(new H2("Luo uusi tunnus"), formLayout, upload, registerButton);
        
        add(formContainer);
    }
}