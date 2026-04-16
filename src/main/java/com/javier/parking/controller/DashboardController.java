package com.javier.parking.controller;

import com.javier.parking.model.ParkingSlot;
import com.javier.parking.model.PaymentMethod;
import com.javier.parking.service.AppSettingsService;
import com.javier.parking.service.HistoryService;
import com.javier.parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private AppSettingsService appSettingsService;

    @Autowired
    private HistoryService historyService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeVehicles", parkingService.findAll());
        model.addAttribute("settings", appSettingsService.getSettings());

        return "dashboard";
    }

    @PostMapping("/dashboard/entry")
    public String registerEntry(@RequestParam String plate, RedirectAttributes redirectAttributes) {
        try {
            ParkingSlot slot = ParkingSlot.builder()
                    .plate(plate.toUpperCase())
                    .build();
            parkingService.registerEntry(slot);
            redirectAttributes.addFlashAttribute("successMessage", "Patente " + plate + " ingresada correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/calculate")
    public String calculateCost(@RequestParam String plate, RedirectAttributes redirectAttributes) {
        try {
            double cost = parkingService.calculateCost(plate);
            redirectAttributes.addFlashAttribute("checkoutPlate", plate);
            redirectAttributes.addFlashAttribute("checkoutCost", cost);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/exit")
    public String registerExit(@RequestParam String plate, @RequestParam PaymentMethod paymentMethod, RedirectAttributes redirectAttributes) {
        try {
            parkingService.registerExit(plate, paymentMethod);
            redirectAttributes.addFlashAttribute("successMessage", "Salida registrada correctamente. Cobro exitoso.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/delete")
    public String delete(@RequestParam String plate, RedirectAttributes ra) {
        try {
            parkingService.deleteEntry(plate);
            ra.addFlashAttribute("successMessage", "Registro eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/cancel-checkout")
    public String cancelCheckout(@RequestParam String plate) {
        parkingService.cancelFreeze(plate);
        return "redirect:/dashboard";
    }
}
