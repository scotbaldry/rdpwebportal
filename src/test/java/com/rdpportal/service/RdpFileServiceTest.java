package com.rdpportal.service;

import com.rdpportal.model.Machine;
import com.rdpportal.model.UserMachineAssignment;
import com.rdpportal.model.User;
import com.rdpportal.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RdpFileServiceTest {

    private RdpFileService rdpFileService;

    @BeforeEach
    void setUp() {
        rdpFileService = new RdpFileService();
    }

    @Test
    void generateRdpContent_defaultPort() {
        Machine machine = new Machine("Test VM", "192.168.1.100");

        String content = rdpFileService.generateRdpContent(machine, null);

        assertTrue(content.startsWith("\uFEFF"));
        assertTrue(content.contains("full address:s:192.168.1.100\r\n"));
        assertTrue(content.contains("screen mode id:i:2\r\n"));
        assertTrue(content.contains("desktopwidth:i:1920\r\n"));
        assertTrue(content.contains("redirectclipboard:i:1\r\n"));
        assertTrue(content.contains("autoreconnection enabled:i:1\r\n"));
        assertFalse(content.contains(":3389"));
    }

    @Test
    void generateRdpContent_customPort() {
        Machine machine = new Machine("Test VM", "10.0.0.5");
        machine.setRdpPort(3390);

        String content = rdpFileService.generateRdpContent(machine, null);

        assertTrue(content.contains("full address:s:10.0.0.5:3390\r\n"));
    }

    @Test
    void generateRdpContent_withAssignment() {
        Machine machine = new Machine("Test VM", "server.local");
        User user = new User("testuser", "pass", "Test User", Role.USER);
        UserMachineAssignment assignment = new UserMachineAssignment(user, machine);
        assignment.setRdpUsername("rdpuser");
        assignment.setRdpDomain("MYDOMAIN");

        String content = rdpFileService.generateRdpContent(machine, assignment);

        assertTrue(content.contains("username:s:rdpuser\r\n"));
        assertTrue(content.contains("domain:s:MYDOMAIN\r\n"));
    }

    @Test
    void generateRdpContent_assignmentWithoutOverrides() {
        Machine machine = new Machine("Test VM", "server.local");
        User user = new User("testuser", "pass", "Test User", Role.USER);
        UserMachineAssignment assignment = new UserMachineAssignment(user, machine);

        String content = rdpFileService.generateRdpContent(machine, assignment);

        assertFalse(content.contains("username:s:"));
        assertFalse(content.contains("domain:s:"));
    }

    @Test
    void getFilename_sanitizesSpecialChars() {
        Machine machine = new Machine("My VM / Test <1>", "host");

        String filename = rdpFileService.getFilename(machine);

        assertEquals("My_VM_Test_1.rdp", filename);
    }

    @Test
    void getFilename_normalName() {
        Machine machine = new Machine("Dev Workstation", "host");

        String filename = rdpFileService.getFilename(machine);

        assertEquals("Dev_Workstation.rdp", filename);
    }
}
