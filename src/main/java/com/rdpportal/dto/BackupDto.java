package com.rdpportal.dto;

import java.util.List;

public class BackupDto {

    private String exportedAt;
    private String siteName;
    private List<UserBackup> users;
    private List<MachineBackup> machines;
    private List<AssignmentBackup> assignments;

    public String getExportedAt() { return exportedAt; }
    public void setExportedAt(String exportedAt) { this.exportedAt = exportedAt; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public List<UserBackup> getUsers() { return users; }
    public void setUsers(List<UserBackup> users) { this.users = users; }

    public List<MachineBackup> getMachines() { return machines; }
    public void setMachines(List<MachineBackup> machines) { this.machines = machines; }

    public List<AssignmentBackup> getAssignments() { return assignments; }
    public void setAssignments(List<AssignmentBackup> assignments) { this.assignments = assignments; }

    public static class UserBackup {
        private String username;
        private String displayName;
        private String role;
        private boolean enabled;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class MachineBackup {
        private String displayName;
        private String hostname;
        private String description;
        private String icon;
        private int rdpPort;
        private boolean enabled;

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public String getHostname() { return hostname; }
        public void setHostname(String hostname) { this.hostname = hostname; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public int getRdpPort() { return rdpPort; }
        public void setRdpPort(int rdpPort) { this.rdpPort = rdpPort; }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class AssignmentBackup {
        private String username;
        private String machineHostname;
        private String rdpUsername;
        private String rdpDomain;
        private String rdpPassword;
        private boolean useMultimon;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getMachineHostname() { return machineHostname; }
        public void setMachineHostname(String machineHostname) { this.machineHostname = machineHostname; }

        public String getRdpUsername() { return rdpUsername; }
        public void setRdpUsername(String rdpUsername) { this.rdpUsername = rdpUsername; }

        public String getRdpDomain() { return rdpDomain; }
        public void setRdpDomain(String rdpDomain) { this.rdpDomain = rdpDomain; }

        public String getRdpPassword() { return rdpPassword; }
        public void setRdpPassword(String rdpPassword) { this.rdpPassword = rdpPassword; }

        public boolean isUseMultimon() { return useMultimon; }
        public void setUseMultimon(boolean useMultimon) { this.useMultimon = useMultimon; }
    }
}
