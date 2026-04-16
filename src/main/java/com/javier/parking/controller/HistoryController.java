package com.javier.parking.controller;

import com.javier.parking.model.ParkingHistory;
import com.javier.parking.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/history")
    public String history(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        List<ParkingHistory> history;

        if (date != null) {
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end   = LocalDateTime.of(date, LocalTime.MAX);
            history = historyService.findByExitTimeBetween(start, end);
        } else {
            history = historyService.findAll();
        }

        double totalEarnings = history.stream().mapToDouble(ParkingHistory::getCost).sum();

        model.addAttribute("history", history);
        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("selectedDate", date);

        return "history";
    }
}