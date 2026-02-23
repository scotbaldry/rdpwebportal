package com.rdpportal.controller;

import com.rdpportal.model.Machine;
import com.rdpportal.model.User;
import com.rdpportal.model.UserMachineAssignment;
import com.rdpportal.repository.UserMachineAssignmentRepository;
import com.rdpportal.repository.UserRepository;
import com.rdpportal.service.RdpFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class RdpController {

    private final UserRepository userRepository;
    private final UserMachineAssignmentRepository assignmentRepository;
    private final RdpFileService rdpFileService;

    public RdpController(UserRepository userRepository,
                         UserMachineAssignmentRepository assignmentRepository,
                         RdpFileService rdpFileService) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.rdpFileService = rdpFileService;
    }

    @GetMapping("/assignments/{id}/rdp")
    public ResponseEntity<byte[]> downloadRdp(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean multimon,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserMachineAssignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not assigned to this machine");
        }

        Machine machine = assignment.getMachine();

        boolean useBat = user.isUseBatLauncher()
                && assignment.getRdpPassword() != null
                && !assignment.getRdpPassword().isBlank();

        String content;
        String filename;
        String contentType;

        if (useBat) {
            content = rdpFileService.generateBatContent(machine, assignment, multimon);
            filename = rdpFileService.getFilename(machine, true);
            contentType = "application/bat";
        } else {
            content = rdpFileService.generateRdpContent(machine, assignment, multimon);
            filename = rdpFileService.getFilename(machine, false);
            contentType = "application/x-rdp";
        }

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType(contentType))
            .contentLength(bytes.length)
            .body(bytes);
    }
}
