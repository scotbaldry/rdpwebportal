package com.rdpportal.dto;

import com.rdpportal.model.Machine;
import com.rdpportal.model.UserMachineAssignment;

public class ConnectionDto {
    private Long assignmentId;
    private Long machineId;
    private String displayName;
    private String hostname;
    private String description;
    private String icon;
    private int rdpPort;
    private String rdpUsername;
    private String rdpDomain;
    private boolean hasPassword;
    private boolean useMultimon;

    public ConnectionDto() {}

    public ConnectionDto(UserMachineAssignment assignment) {
        Machine machine = assignment.getMachine();
        this.assignmentId = assignment.getId();
        this.machineId = machine.getId();
        this.displayName = machine.getDisplayName();
        this.hostname = machine.getHostname();
        this.description = machine.getDescription();
        this.icon = machine.getIcon();
        this.rdpPort = machine.getRdpPort();
        this.rdpUsername = assignment.getRdpUsername();
        this.rdpDomain = assignment.getRdpDomain();
        this.hasPassword = assignment.getRdpPassword() != null && !assignment.getRdpPassword().isBlank();
        this.useMultimon = assignment.isUseMultimon();
    }

    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }

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

    public String getRdpUsername() { return rdpUsername; }
    public void setRdpUsername(String rdpUsername) { this.rdpUsername = rdpUsername; }

    public String getRdpDomain() { return rdpDomain; }
    public void setRdpDomain(String rdpDomain) { this.rdpDomain = rdpDomain; }

    public boolean isHasPassword() { return hasPassword; }
    public void setHasPassword(boolean hasPassword) { this.hasPassword = hasPassword; }

    public boolean isUseMultimon() { return useMultimon; }
    public void setUseMultimon(boolean useMultimon) { this.useMultimon = useMultimon; }
}
