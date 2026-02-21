package com.rdpportal.controller;

import com.rdpportal.model.AppSettings;
import com.rdpportal.repository.AppSettingsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final AppSettingsRepository appSettingsRepository;

    public AuthController(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    @GetMapping("/login")
    public String login(Model model) {
        String siteName = appSettingsRepository.findById(1L)
                .map(AppSettings::getSiteName)
                .orElse("RDP Web Portal");
        model.addAttribute("siteName", siteName);
        return "login";
    }
}
