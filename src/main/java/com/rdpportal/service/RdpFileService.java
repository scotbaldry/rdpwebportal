package com.rdpportal.service;

import com.rdpportal.model.Machine;
import com.rdpportal.model.UserMachineAssignment;
import org.springframework.stereotype.Service;

@Service
public class RdpFileService {

    private static final String BOM = "\uFEFF";

    public String generateRdpContent(Machine machine, UserMachineAssignment assignment, boolean multimon) {
        StringBuilder sb = new StringBuilder();
        sb.append(BOM);

        String address = machine.getHostname();
        if (machine.getRdpPort() != 3389) {
            address += ":" + machine.getRdpPort();
        }

        sb.append("full address:s:").append(address).append("\r\n");
        sb.append("screen mode id:i:2").append("\r\n");
        if (!multimon) {
            sb.append("desktopwidth:i:1920").append("\r\n");
            sb.append("desktopheight:i:1080").append("\r\n");
        }
        sb.append("session bpp:i:32").append("\r\n");
        sb.append("use multimon:i:").append(multimon ? "1" : "0").append("\r\n");
        sb.append("audiomode:i:0").append("\r\n");
        sb.append("redirectclipboard:i:1").append("\r\n");
        sb.append("redirectprinters:i:0").append("\r\n");
        sb.append("redirectsmartcards:i:0").append("\r\n");
        sb.append("autoreconnection enabled:i:1").append("\r\n");
        sb.append("authentication level:i:2").append("\r\n");
        sb.append("prompt for credentials:i:0").append("\r\n");
        sb.append("negotiate security layer:i:1").append("\r\n");
        sb.append("connection type:i:7").append("\r\n");
        sb.append("networkautodetect:i:1").append("\r\n");
        sb.append("bandwidthautodetect:i:1").append("\r\n");
        sb.append("enablecredsspsupport:i:1").append("\r\n");

        if (assignment != null) {
            if (assignment.getRdpUsername() != null && !assignment.getRdpUsername().isBlank()) {
                sb.append("username:s:").append(assignment.getRdpUsername()).append("\r\n");
            }
            if (assignment.getRdpDomain() != null && !assignment.getRdpDomain().isBlank()) {
                sb.append("domain:s:").append(assignment.getRdpDomain()).append("\r\n");
            }
        }

        return sb.toString();
    }

    public String generateBatContent(Machine machine, UserMachineAssignment assignment, boolean multimon) {
        String rdpContent = generateRdpContent(machine, assignment, multimon);
        String safeName = getSafeName(machine);

        String address = machine.getHostname();
        if (machine.getRdpPort() != 3389) {
            address += ":" + machine.getRdpPort();
        }

        // Build the credential target matching what mstsc expects
        String credTarget = "TERMSRV/" + machine.getHostname();

        String rdpUser = "";
        if (assignment != null && assignment.getRdpUsername() != null && !assignment.getRdpUsername().isBlank()) {
            rdpUser = assignment.getRdpUsername();
            if (assignment.getRdpDomain() != null && !assignment.getRdpDomain().isBlank()) {
                rdpUser = assignment.getRdpDomain() + "\\" + rdpUser;
            }
        }

        String password = (assignment != null && assignment.getRdpPassword() != null) ? assignment.getRdpPassword() : "";

        StringBuilder bat = new StringBuilder();
        bat.append("@echo off\r\n");
        bat.append("setlocal\r\n");
        bat.append("\r\n");

        // Write the .rdp content to a temp file
        bat.append("set \"RDPFILE=%TEMP%\\").append(safeName).append(".rdp\"\r\n");
        bat.append("\r\n");

        // Write RDP file line by line (skip BOM for bat-generated file)
        String rdpBody = rdpContent.replace(BOM, "");
        boolean first = true;
        for (String line : rdpBody.split("\r\n")) {
            if (!line.isEmpty()) {
                bat.append("echo ").append(escapeForBat(line));
                bat.append(first ? "> \"%RDPFILE%\"\r\n" : ">> \"%RDPFILE%\"\r\n");
                first = false;
            }
        }
        bat.append("\r\n");

        // Store credentials temporarily using cmdkey
        bat.append("cmdkey /generic:\"").append(credTarget).append("\" /user:\"").append(rdpUser).append("\" /pass:\"").append(password).append("\"\r\n");
        bat.append("\r\n");

        // Launch mstsc
        bat.append("mstsc \"%RDPFILE%\"\r\n");
        bat.append("\r\n");

        // Wait a moment for mstsc to read creds, then clean up
        bat.append("timeout /t 5 /nobreak >nul\r\n");
        bat.append("cmdkey /delete:\"").append(credTarget).append("\" >nul 2>&1\r\n");
        bat.append("del \"%RDPFILE%\" >nul 2>&1\r\n");
        bat.append("endlocal\r\n");

        return bat.toString();
    }

    private String escapeForBat(String line) {
        // Escape special batch characters
        return line.replace("%", "%%")
                   .replace("^", "^^")
                   .replace("&", "^&")
                   .replace("<", "^<")
                   .replace(">", "^>")
                   .replace("|", "^|");
    }

    private String getSafeName(Machine machine) {
        return machine.getDisplayName()
            .replaceAll("[^a-zA-Z0-9._\\- ]", "")
            .replaceAll("\\s+", "_");
    }

    public String getFilename(Machine machine, boolean batLauncher) {
        String safeName = getSafeName(machine);
        return safeName + (batLauncher ? ".bat" : ".rdp");
    }

    public String getFilename(Machine machine) {
        return getFilename(machine, false);
    }
}
