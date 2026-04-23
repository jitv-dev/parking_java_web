package com.javier.parking.controller.rest;

import com.javier.parking.model.ParkingHistory;
import com.javier.parking.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryRestController {
    @Autowired
    private HistoryService historyService;

    @GetMapping
    public ResponseEntity<?> getHistory(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ParkingHistory> history;

            if (date != null) {
                LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
                history = historyService.findByExitTimeBetween(start, end);
            } else {
                history = historyService.findAll();
            }

            double totalEarnings = history.stream().mapToDouble(ParkingHistory::getCost).sum();

            return ResponseEntity.ok(Map.of(
                    "history", history,
                    "totalEarnings", totalEarnings
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHistoryRecord(@PathVariable Long id) {
        try {
            historyService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Registro eliminado del historial correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
