package com.javier.parking.service;

import com.javier.parking.model.AppSettings;
import com.javier.parking.model.ParkingHistory;
import com.javier.parking.model.ParkingSlot;
import com.javier.parking.model.PaymentMethod;
import com.javier.parking.repository.ParkingHistoryRepository;
import com.javier.parking.repository.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingService {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    @Autowired
    private ParkingHistoryRepository parkingHistoryRepository;
    @Autowired
    private AppSettingsService appSettingsService;

    private record DatosBloqueados(double costo, double minutes, LocalDateTime exitTime, LocalDateTime expiresAt) {}
    private final Map<String, DatosBloqueados> datosBloqueados = new HashMap<>();

    public List<ParkingSlot> findAll() {
        return parkingSlotRepository.findAll();
    }

    public ParkingSlot registerEntry(ParkingSlot parkingSlot) {
        if (parkingSlotRepository.findByPlate(parkingSlot.getPlate()).isPresent()) {
            throw new RuntimeException("Patente ya registrada");
        }
        return parkingSlotRepository.save(parkingSlot);
    }

    public ParkingSlot findByPlate(String plate) {
        return parkingSlotRepository.findByPlate(plate)
                .orElseThrow(() -> new RuntimeException("Patente no encontrada: " + plate));
    }

    public double calculateCost(String plate) {
        ParkingSlot slot = findByPlate(plate);
        AppSettings settings = appSettingsService.getSettings();
        LocalDateTime exitTime = LocalDateTime.now();
        double minutes = Duration.between(slot.getEntryTime(), exitTime).toSeconds() / 60.0;
        double costo = minutes * settings.getCostPerMinute();
        datosBloqueados.put(plate, new DatosBloqueados(costo, minutes, exitTime, LocalDateTime.now().plusMinutes(5)));
        return costo;
    }

    public void cancelFreeze(String plate) {
        datosBloqueados.remove(plate);
    }

    public void registerExit(String plate, PaymentMethod paymentMethod) {
        ParkingSlot slot = findByPlate(plate);

        DatosBloqueados datos = datosBloqueados.get(plate);

        if (datos == null || LocalDateTime.now().isAfter(datos.expiresAt())) {
            datosBloqueados.remove(plate);
            throw new RuntimeException("El costo expiró o no fue calculado, consulte nuevamente");
        }

        ParkingHistory history = ParkingHistory.builder()
                .plate(slot.getPlate())
                .entryTime(slot.getEntryTime())
                .exitTime(datos.exitTime())
                .minutes(datos.minutes())
                .cost(datos.costo())
                .paymentMethod(paymentMethod)
                .build();

        parkingHistoryRepository.save(history);
        parkingSlotRepository.delete(slot);
        datosBloqueados.remove(plate);
    }

    public void deleteEntry(String plate) {
        ParkingSlot slot = findByPlate(plate);
        long minutos = Duration.between(slot.getEntryTime(), LocalDateTime.now()).toMinutes();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && minutos > 2) {
            throw new RuntimeException("Seguridad: Han pasado más de 2 minutos. Solo un Administrador puede eliminar este registro.");
        }

        parkingSlotRepository.delete(slot);
        datosBloqueados.remove(plate);
    }
}