package com.rdpportal.controller;

import com.rdpportal.dto.ConnectionDto;
import com.rdpportal.dto.UserDto;
import com.rdpportal.service.MachineService;
import com.rdpportal.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final MachineService machineService;
    private final UserService userService;

    public DashboardController(MachineService machineService, UserService userService) {
        this.machineService = machineService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername());
    }

    @GetMapping("/machines")
    public List<ConnectionDto> myMachines(@AuthenticationPrincipal UserDetails userDetails) {
        return machineService.getConnectionsForUser(userDetails.getUsername());
    }
}
