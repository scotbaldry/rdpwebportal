package com.rdpportal.controller;

import com.rdpportal.dto.*;
import com.rdpportal.service.MachineService;
import com.rdpportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final UserService userService;
    private final MachineService machineService;

    public AdminApiController(UserService userService, MachineService machineService) {
        this.userService = userService;
        this.machineService = machineService;
    }

    // ---- Users ----

    @GetMapping("/users")
    public List<UserDto> listUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/users/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody CreateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Machines ----

    @GetMapping("/machines")
    public List<MachineDto> listMachines() {
        return machineService.getAllMachines();
    }

    @GetMapping("/machines/{id}")
    public MachineDto getMachine(@PathVariable Long id) {
        return machineService.getMachineById(id);
    }

    @PostMapping("/machines")
    public MachineDto createMachine(@RequestBody CreateMachineRequest request) {
        return machineService.createMachine(request);
    }

    @PutMapping("/machines/{id}")
    public MachineDto updateMachine(@PathVariable Long id, @RequestBody CreateMachineRequest request) {
        return machineService.updateMachine(id, request);
    }

    @DeleteMapping("/machines/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable Long id) {
        machineService.deleteMachine(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Assignments ----

    @GetMapping("/assignments")
    public List<AssignmentDto> listAssignments() {
        return machineService.getAllAssignments();
    }

    @PostMapping("/assignments")
    public AssignmentDto createAssignment(@RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        Long machineId = ((Number) body.get("machineId")).longValue();
        String rdpUsername = (String) body.get("rdpUsername");
        String rdpDomain = (String) body.get("rdpDomain");
        String rdpPassword = (String) body.get("rdpPassword");
        boolean useMultimon = Boolean.TRUE.equals(body.get("useMultimon"));
        return machineService.createAssignment(userId, machineId, rdpUsername, rdpDomain, rdpPassword, useMultimon);
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        machineService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
