package com.javier.parking.controller.rest;

import com.javier.parking.model.ParkingSlot;
import com.javier.parking.model.PaymentMethod;
import com.javier.parking.service.AppSettingsService;
import com.javier.parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/parking")
public class DashboardRestController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private AppSettingsService appSettingsService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        try {
            return ResponseEntity.ok(Map.of(
                    "activeVehicles", parkingService.findAll(),
                    "costPerMinute", appSettingsService.getSettings().getCostPerMinute()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/entry")
    public ResponseEntity<?> registerEntry(@RequestBody Map<String, String> body) {
        try {
            ParkingSlot slot = ParkingSlot.builder()
                    .plate(body.get("plate").toUpperCase())
                    .build();
            ParkingSlot saved = parkingService.registerEntry(slot);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/calculate/{plate}")
    public ResponseEntity<?> calculateCost(@PathVariable String plate) {
        try {
            double cost = parkingService.calculateCost(plate);
            return ResponseEntity.ok(Map.of("plate", plate, "cost", cost));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/exit")
    public ResponseEntity<?> registerExit(@RequestBody Map<String, String> body) {
        try {
            parkingService.registerExit(
                body.get("plate"),
                PaymentMethod.valueOf(body.get("paymentMethod"))
            );
            return ResponseEntity.ok(Map.of("message", "Salida registrada correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{plate}")
    public ResponseEntity<?> deleteEntry(@PathVariable String plate) {
        try {
            parkingService.deleteEntry(plate);
            return ResponseEntity.ok(Map.of("message", "Registro eliminado correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cancel/{plate}")
    public ResponseEntity<?> cancelFreeze(@PathVariable String plate) {
        try {
            parkingService.cancelFreeze(plate);
            return ResponseEntity.ok(Map.of("message", "Cotización cancelada."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
