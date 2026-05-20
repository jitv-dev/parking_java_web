package com.javier.parking.controller.rest;

import com.javier.parking.service.AppSettingsService;
import com.javier.parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    @Autowired
    private UserService userService;

    @Autowired
    private AppSettingsService appSettingsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getSettings() {
        try {
            // En REST no necesitas el DTO en la respuesta porque React construye su propio formulario
            return ResponseEntity.ok(Map.of(
                    "users", userService.findAll(),
                    "settings", appSettingsService.getSettings().getCostPerMinute()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
