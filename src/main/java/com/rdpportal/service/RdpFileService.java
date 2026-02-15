package com.rdpportal.service;

import com.rdpportal.model.Machine;
import com.rdpportal.model.UserMachineAssignment;
import org.springframework.stereotype.Service;

@Service
public class RdpFileService {

    private static final String BOM = "\uFEFF";

    public String generateRdpContent(Machine machine, UserMachineAssignment assignment) {
        StringBuilder sb = new StringBuilder();
        sb.append(BOM);

        String address = machine.getHostname();
        if (machine.getRdpPort() != 3389) {
            address += ":" + machine.getRdpPort();
        }

        boolean multimon = assignment != null && assignment.isUseMultimon();

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

    /**
     * Generates a .bat launcher that stores credentials via cmdkey,
     * writes a temp .rdp file, launches mstsc, then cleans up.
     * This works cross-platform (server can be Linux) because cmdkey
     * runs on the Windows client machine.
     */
    public String generateBatLauncher(Machine machine, UserMachineAssignment assignment) {
        String address = machine.getHostname();
        if (machine.getRdpPort() != 3389) {
            address += ":" + machine.getRdpPort();
        }

        String rdpContent = generateRdpContent(machine, assignment);
        // Remove BOM for bat embedding — we'll write it via echo
        if (rdpContent.startsWith(BOM)) {
            rdpContent = rdpContent.substring(1);
        }

        String username = assignment.getRdpUsername() != null ? assignment.getRdpUsername() : "";
        String domain = assignment.getRdpDomain() != null ? assignment.getRdpDomain() : "";
        String password = assignment.getRdpPassword();

        // Build the credential target — this is what mstsc looks up
        String credTarget = "TERMSRV/" + machine.getHostname();

        // Build the full user for cmdkey (DOMAIN\\user or just user)
        String fullUser = username;
        if (!domain.isEmpty() && !username.isEmpty()) {
            fullUser = domain + "\\" + username;
        }

        StringBuilder bat = new StringBuilder();
        bat.append("@echo off\r\n");
        bat.append("setlocal\r\n");

        // Store credentials in Windows Credential Manager
        bat.append("cmdkey /generic:\"").append(credTarget).append("\"");
        bat.append(" /user:\"").append(batEscape(fullUser)).append("\"");
        bat.append(" /pass:\"").append(batEscape(password)).append("\"\r\n");

        // Write the RDP file to a temp location
        bat.append("set \"RDPFILE=%TEMP%\\rdpportal_").append(System.currentTimeMillis()).append(".rdp\"\r\n");

        // Write each line of the RDP content
        String[] lines = rdpContent.split("\r\n");
        boolean first = true;
        for (String line : lines) {
            if (line.isEmpty()) continue;
            String escaped = batEscape(line);
            if (first) {
                bat.append("echo ").append(escaped).append(" > \"%RDPFILE%\"\r\n");
                first = false;
            } else {
                bat.append("echo ").append(escaped).append(" >> \"%RDPFILE%\"\r\n");
            }
        }

        // Launch mstsc and wait briefly, then clean up
        bat.append("start \"\" mstsc \"%RDPFILE%\"\r\n");
        bat.append("timeout /t 5 /nobreak >nul 2>&1\r\n");
        bat.append("del \"%RDPFILE%\" >nul 2>&1\r\n");
        bat.append("endlocal\r\n");

        return bat.toString();
    }

    /**
     * Returns true if the assignment has a stored password (meaning we should
     * generate a .bat launcher instead of a plain .rdp file).
     */
    public boolean hasPassword(UserMachineAssignment assignment) {
        return assignment != null
            && assignment.getRdpPassword() != null
            && !assignment.getRdpPassword().isBlank();
    }

    public String getFilename(Machine machine) {
        String safeName = machine.getDisplayName()
            .replaceAll("[^a-zA-Z0-9._\\- ]", "")
            .replaceAll("\\s+", "_");
        return safeName + ".rdp";
    }

    public String getBatFilename(Machine machine) {
        String safeName = machine.getDisplayName()
            .replaceAll("[^a-zA-Z0-9._\\- ]", "")
            .replaceAll("\\s+", "_");
        return safeName + ".bat";
    }

    /** Escapes special batch characters so echo outputs them literally. */
    private String batEscape(String s) {
        if (s == null) return "";
        return s.replace("^", "^^")
                .replace("&", "^&")
                .replace("<", "^<")
                .replace(">", "^>")
                .replace("|", "^|")
                .replace("%", "%%");
    }
}
