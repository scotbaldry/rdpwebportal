package com.rdpportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdpportal.dto.*;
import com.rdpportal.model.AppSettings;
import com.rdpportal.repository.AppSettingsRepository;
import com.rdpportal.model.Machine;
import com.rdpportal.model.Role;
import com.rdpportal.model.User;
import com.rdpportal.model.UserMachineAssignment;
import com.rdpportal.repository.MachineRepository;
import com.rdpportal.repository.UserMachineAssignmentRepository;
import com.rdpportal.repository.UserRepository;
import com.rdpportal.service.MachineService;
import com.rdpportal.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final UserService userService;
    private final MachineService machineService;
    private final UserRepository userRepository;
    private final MachineRepository machineRepository;
    private final UserMachineAssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final AppSettingsRepository appSettingsRepository;

    public AdminApiController(UserService userService, MachineService machineService,
                              UserRepository userRepository, MachineRepository machineRepository,
                              UserMachineAssignmentRepository assignmentRepository,
                              PasswordEncoder passwordEncoder, ObjectMapper objectMapper,
                              AppSettingsRepository appSettingsRepository) {
        this.userService = userService;
        this.machineService = machineService;
        this.userRepository = userRepository;
        this.machineRepository = machineRepository;
        this.assignmentRepository = assignmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.appSettingsRepository = appSettingsRepository;
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

    // ---- Site Settings ----

    @GetMapping("/settings")
    public Map<String, Object> getSettings() {
        AppSettings s = appSettingsRepository.findById(1L).orElse(new AppSettings());
        return Map.of("siteName", s.getSiteName());
    }

    @PutMapping("/settings")
    public Map<String, Object> updateSettings(@RequestBody Map<String, String> body) {
        AppSettings s = appSettingsRepository.findById(1L).orElseGet(AppSettings::new);
        String name = body.getOrDefault("siteName", "").strip();
        if (!name.isBlank()) s.setSiteName(name);
        appSettingsRepository.save(s);
        return Map.of("siteName", s.getSiteName());
    }

    // ---- Backup ----

    @GetMapping("/backup")
    public ResponseEntity<BackupDto> backup() {
        BackupDto backup = new BackupDto();
        backup.setExportedAt(Instant.now().toString());

        backup.setUsers(userRepository.findAll().stream().map(u -> {
            BackupDto.UserBackup ub = new BackupDto.UserBackup();
            ub.setUsername(u.getUsername());
            ub.setDisplayName(u.getDisplayName());
            ub.setRole(u.getRole().name());
            ub.setEnabled(u.isEnabled());
            return ub;
        }).collect(Collectors.toList()));

        backup.setMachines(machineRepository.findAll().stream().map(m -> {
            BackupDto.MachineBackup mb = new BackupDto.MachineBackup();
            mb.setDisplayName(m.getDisplayName());
            mb.setHostname(m.getHostname());
            mb.setDescription(m.getDescription());
            mb.setIcon(m.getIcon());
            mb.setRdpPort(m.getRdpPort());
            mb.setEnabled(m.isEnabled());
            return mb;
        }).collect(Collectors.toList()));

        backup.setAssignments(assignmentRepository.findAll().stream().map(a -> {
            BackupDto.AssignmentBackup ab = new BackupDto.AssignmentBackup();
            ab.setUsername(a.getUser().getUsername());
            ab.setMachineHostname(a.getMachine().getHostname());
            ab.setRdpUsername(a.getRdpUsername());
            ab.setRdpDomain(a.getRdpDomain());
            ab.setRdpPassword(a.getRdpPassword());
            ab.setUseMultimon(a.isUseMultimon());
            return ab;
        }).collect(Collectors.toList()));

        String filename = "rdpportal-backup-" + LocalDate.now() + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(backup);
    }

    @PostMapping("/restore")
    @Transactional
    public ResponseEntity<Map<String, Object>> restore(@RequestParam("file") MultipartFile file) throws Exception {
        BackupDto backup = objectMapper.readValue(file.getInputStream(), BackupDto.class);

        // 1. Wipe all assignments and machines; preserve admin users
        assignmentRepository.deleteAll();
        machineRepository.deleteAll();
        userRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .forEach(userRepository::delete);

        // 2. Restore machines
        Map<String, Machine> machinesByHostname = new HashMap<>();
        for (BackupDto.MachineBackup mb : backup.getMachines()) {
            Machine m = new Machine(mb.getDisplayName(), mb.getHostname());
            m.setDescription(mb.getDescription());
            m.setIcon(mb.getIcon());
            m.setRdpPort(mb.getRdpPort());
            m.setEnabled(mb.isEnabled());
            machinesByHostname.put(mb.getHostname(), machineRepository.save(m));
        }

        // 3. Restore non-admin users (default password = username; admins from backup are skipped)
        Map<String, User> usersByUsername = new HashMap<>();
        userRepository.findAll().forEach(u -> usersByUsername.put(u.getUsername(), u));

        int restoredUsers = 0;
        for (BackupDto.UserBackup ub : backup.getUsers()) {
            if (Role.ADMIN.name().equals(ub.getRole())) continue;
            if (usersByUsername.containsKey(ub.getUsername())) continue;
            User u = new User(ub.getUsername(), passwordEncoder.encode(ub.getUsername()),
                    ub.getDisplayName(), Role.valueOf(ub.getRole()));
            u.setEnabled(ub.isEnabled());
            usersByUsername.put(ub.getUsername(), userRepository.save(u));
            restoredUsers++;
        }

        // 4. Restore assignments
        int restoredAssignments = 0;
        for (BackupDto.AssignmentBackup ab : backup.getAssignments()) {
            User user = usersByUsername.get(ab.getUsername());
            Machine machine = machinesByHostname.get(ab.getMachineHostname());
            if (user == null || machine == null) continue;
            UserMachineAssignment a = new UserMachineAssignment(user, machine);
            a.setRdpUsername(ab.getRdpUsername());
            a.setRdpDomain(ab.getRdpDomain());
            a.setRdpPassword(ab.getRdpPassword());
            a.setUseMultimon(ab.isUseMultimon());
            assignmentRepository.save(a);
            restoredAssignments++;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("machines", machinesByHostname.size());
        result.put("users", restoredUsers);
        result.put("assignments", restoredAssignments);
        result.put("note", restoredUsers > 0
                ? "Restored users have their username set as their temporary password."
                : "No non-admin users were restored.");
        return ResponseEntity.ok(result);
    }
}
