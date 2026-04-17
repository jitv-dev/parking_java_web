package com.javier.parking.controller;

import com.javier.parking.dto.UserDTO;
import com.javier.parking.model.User;
import com.javier.parking.service.AppSettingsService;
import com.javier.parking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppSettingsService appSettingsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("settings", appSettingsService.getSettings());
        model.addAttribute("userDTO", new UserDTO());
        return "admin/settings";
    }

    @PostMapping("/settings/create")
    public String createUser(@Valid @ModelAttribute UserDTO userDTO,
                             BindingResult result,
                             RedirectAttributes ra,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            model.addAttribute("settings", appSettingsService.getSettings());
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("formErrors", result.getAllErrors());
            return "admin/settings";
        }
        try {
            userService.save(User.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .role(userDTO.getRole())
                    .enabled(true)
                    .build());
            ra.addFlashAttribute("successMessage", "Usuario '" + userDTO.getUsername() + "' creado correctamente.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @PostMapping("/settings/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.delete(id);
            ra.addFlashAttribute("successMessage", "Usuario eliminado correctamente.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @PostMapping("/settings/toggle/{id}")
    public String toggleUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            User user = userService.findById(id);
            user.setEnabled(!user.isEnabled());
            userService.update(id, user);
            String estado = user.isEnabled() ? "habilitado" : "deshabilitado";
            ra.addFlashAttribute("successMessage", "Usuario " + estado + " correctamente.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @PostMapping("/settings/cost")
    public String updateCost(@RequestParam Double costPerMinute, RedirectAttributes ra) {
        try {
            appSettingsService.updateCost(costPerMinute);
            ra.addFlashAttribute("successMessage", "Costo actualizado a $" + costPerMinute + " por minuto.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/settings";
    }
}