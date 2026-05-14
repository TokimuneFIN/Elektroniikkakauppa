package fi.kaius.vaadin.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fi.kaius.vaadin.data.entity.Role;
import fi.kaius.vaadin.data.entity.User;
import fi.kaius.vaadin.data.repository.UserRepository;
import fi.kaius.vaadin.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.util.Set;

@PageTitle("Hallinta")
@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {

    private final UserRepository userRepository;
    private final Grid<User> grid = new Grid<>(User.class, false);

    public AdminView(UserRepository userRepository) {
        this.userRepository = userRepository;
        setSizeFull();

        add(new H2("Käyttäjien hallinta"));

        grid.addColumn(User::getUsername).setHeader("Käyttäjätunnus");
        grid.addColumn(User::getName).setHeader("Nimi");
        grid.addColumn(User::getEmail).setHeader("Sähköposti");
        grid.addColumn(u -> u.getRoles().toString()).setHeader("Roolit");

        grid.addComponentColumn(user -> {
            Button editBtn = new Button("Muokkaa rooleja", e -> avaaRooliDialogi(user));
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return editBtn;
        }).setHeader("Toiminnot");

        grid.setItems(userRepository.findAll());
        add(grid);
    }

    private void avaaRooliDialogi(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Muokkaa käyttäjän " + user.getUsername() + " rooleja");

        CheckboxGroup<Role> roleGroup = new CheckboxGroup<>("Valitse roolit");
        roleGroup.setItems(Role.values());
        roleGroup.setValue(user.getRoles());

        Button saveBtn = new Button("Tallenna", e -> {
            user.setRoles(roleGroup.getValue());
            userRepository.save(user); 
            grid.setItems(userRepository.findAll());
            Notification.show("Roolit päivitetty!");
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Peruuta", e -> dialog.close());

        dialog.add(roleGroup);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }
}