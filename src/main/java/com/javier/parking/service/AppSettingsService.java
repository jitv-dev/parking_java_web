package com.javier.parking.service;

import com.javier.parking.model.AppSettings;
import com.javier.parking.repository.AppSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppSettingsService {

    @Autowired
    private AppSettingsRepository appSettingsRepository;

    public AppSettings getSettings() {
        return appSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Configuracion no encontrada"));
    }

    public AppSettings updateCost(Double newCost) {
        AppSettings settings = getSettings();
        settings.setCostPerMinute(newCost);
        return appSettingsRepository.save(settings);
    }
}
