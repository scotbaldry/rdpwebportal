package com.rdpportal.dto;

import com.rdpportal.model.UserMachineAssignment;

public class AssignmentDto {
    private Long id;
    private Long userId;
    private String username;
    private Long machineId;
    private String machineName;
    private String rdpUsername;
    private String rdpDomain;
    private boolean hasPassword;
    private boolean useMultimon;

    public AssignmentDto() {}

    public AssignmentDto(UserMachineAssignment assignment) {
        this.id = assignment.getId();
        this.userId = assignment.getUser().getId();
        this.username = assignment.getUser().getUsername();
        this.machineId = assignment.getMachine().getId();
        this.machineName = assignment.getMachine().getDisplayName();
        this.rdpUsername = assignment.getRdpUsername();
        this.rdpDomain = assignment.getRdpDomain();
        this.hasPassword = assignment.getRdpPassword() != null && !assignment.getRdpPassword().isBlank();
        this.useMultimon = assignment.isUseMultimon();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }

    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }

    public String getRdpUsername() { return rdpUsername; }
    public void setRdpUsername(String rdpUsername) { this.rdpUsername = rdpUsername; }

    public String getRdpDomain() { return rdpDomain; }
    public void setRdpDomain(String rdpDomain) { this.rdpDomain = rdpDomain; }

    public boolean isHasPassword() { return hasPassword; }
    public void setHasPassword(boolean hasPassword) { this.hasPassword = hasPassword; }

    public boolean isUseMultimon() { return useMultimon; }
    public void setUseMultimon(boolean useMultimon) { this.useMultimon = useMultimon; }
}
