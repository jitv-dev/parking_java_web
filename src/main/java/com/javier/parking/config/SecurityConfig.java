package com.javier.parking.config;

import com.javier.parking.model.AppSettings;
import com.javier.parking.model.Role;
import com.javier.parking.model.User;
import com.javier.parking.repository.AppSettingsRepository;
import com.javier.parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/**", "/parking/**", "/history/**").hasAnyRole("ADMIN", "WORKER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public CommandLineRunner initData(AppSettingsRepository settingsRepository) {
        return args -> {
            if (userService.findAll().isEmpty()) {
                userService.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("Admin123"))
                        .role(Role.ROLE_ADMIN)
                        .enabled(true)
                        .build());

                userService.save(User.builder()
                        .username("worker")
                        .password(passwordEncoder().encode("Worker123"))
                        .role(Role.ROLE_WORKER)
                        .enabled(true)
                        .build());
            }

            if (settingsRepository.findAll().isEmpty()) {
                settingsRepository.save(AppSettings.builder()
                        .costPerMinute(100.0)
                        .build());
            }
        };
    }
}