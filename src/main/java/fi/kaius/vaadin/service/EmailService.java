package fi.kaius.vaadin.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    private final String ADMIN_EMAIL = "s2313803@edu.savonia.fi";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void lähetäSalasananPalautus(String vastaanottaja, String token) {
        try {
            String palautusLinkki = "http://localhost:8080/reset-password?token=" + token;
            SimpleMailMessage viesti = new SimpleMailMessage();
            viesti.setTo(vastaanottaja);
            viesti.setSubject("Salasanan palautus - Elektroniikkakauppa");
            viesti.setText("Olet pyytänyt salasanan palautusta.\n\n" +
                          "Voit asettaa uuden salasanan klikkaamalla alla olevaa linkkiä:\n" +
                          palautusLinkki + "\n\n" +
                          "Linkki on voimassa 15 minuuttia.");
            mailSender.send(viesti);
            logger.info("Palautuslinkki lähetetty osoitteeseen: {}", vastaanottaja);
        } catch (Exception e) {
            logger.error("Virhe sähköpostin lähetyksessä: {}", e.getMessage());
        }
    }

    public void lähhetäRekisteröintiSähköposti(String vastaanottaja, String käyttäjätunnus) {
        try {
            SimpleMailMessage viesti = new SimpleMailMessage();
            viesti.setTo(vastaanottaja);
            viesti.setSubject("Tervetuloa Elektroniikkakauppaan!");
            viesti.setText("Hei " + käyttäjätunnus + ",\n\nKiitos rekisteröitymisestäsi sovellukseemme.");
            mailSender.send(viesti);
        } catch (Exception e) {
            logger.error("Virhe rekisteröintiviestin lähetyksessä: {}", e.getMessage());
        }
    }

    public void lähhetäYlläpitäjäIlmoitus(String käyttäjätunnus) {
        try {
            SimpleMailMessage viesti = new SimpleMailMessage();
            viesti.setTo(ADMIN_EMAIL);
            viesti.setSubject("Uusi käyttäjä rekisteröitynyt");
            viesti.setText("Järjestelmään on luotu uusi käyttäjä: " + käyttäjätunnus);
            mailSender.send(viesti);
            logger.info("Ylläpitäjäilmoitus lähetetty osoitteeseen: {}", ADMIN_EMAIL);
        } catch (Exception e) {
            logger.error("Virhe ylläpitäjäilmoituksen lähetyksessä: {}", e.getMessage());
        }
    }
}