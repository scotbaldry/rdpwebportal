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
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserMachineAssignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (!assignment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not assigned to this machine");
        }

        Machine machine = assignment.getMachine();

        if (rdpFileService.hasPassword(assignment)) {
            // Generate a .bat launcher that stores credentials via cmdkey
            String content = rdpFileService.generateBatLauncher(machine, assignment);
            String filename = rdpFileService.getBatFilename(machine);
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytes.length)
                .body(bytes);
        }

        // No password — serve a standard .rdp file
        String content = rdpFileService.generateRdpContent(machine, assignment);
        String filename = rdpFileService.getFilename(machine);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/x-rdp"))
            .contentLength(bytes.length)
            .body(bytes);
    }
}
