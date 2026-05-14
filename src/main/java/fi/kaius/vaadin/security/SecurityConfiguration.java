package fi.kaius.vaadin.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import fi.kaius.vaadin.views.login.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(new AntPathRequestMatcher("/registration")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/login/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/oauth2/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/forgot-password/**")).permitAll()
            
        );
        http.exceptionHandling(exception -> exception
        .accessDeniedPage("/access-denied")
        );
        http.oauth2Login(oauth2 -> oauth2
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        );

        http.authorizeHttpRequests(auth -> auth
        .requestMatchers(new AntPathRequestMatcher("/registration")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/forgot-password")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/reset-password")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
         );

        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        setLoginView(http, LoginView.class);
        super.configure(http);
    }
}