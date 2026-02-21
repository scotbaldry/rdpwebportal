package com.rdpportal.controller;

import com.rdpportal.dto.ConnectionDto;
import com.rdpportal.dto.UserDto;
import com.rdpportal.model.AppSettings;
import com.rdpportal.repository.AppSettingsRepository;
import com.rdpportal.service.MachineService;
import com.rdpportal.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final MachineService machineService;
    private final UserService userService;
    private final AppSettingsRepository appSettingsRepository;

    public DashboardController(MachineService machineService, UserService userService,
                               AppSettingsRepository appSettingsRepository) {
        this.machineService = machineService;
        this.userService = userService;
        this.appSettingsRepository = appSettingsRepository;
    }

    @GetMapping("/settings")
    public Map<String, Object> getSettings() {
        AppSettings s = appSettingsRepository.findById(1L).orElse(new AppSettings());
        return Map.of("siteName", s.getSiteName());
    }

    @GetMapping("/me")
    public UserDto currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername());
    }

    @GetMapping("/machines")
    public List<ConnectionDto> myMachines(@AuthenticationPrincipal UserDetails userDetails) {
        return machineService.getConnectionsForUser(userDetails.getUsername());
    }

    @PutMapping("/me/settings")
    public UserDto updateSettings(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody Map<String, Object> body) {
        return userService.updateSettings(userDetails.getUsername(), body);
    }
}
