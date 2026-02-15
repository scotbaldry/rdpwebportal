package com.rdpportal.dto;

import com.rdpportal.model.Machine;

public class MachineDto {
    private Long id;
    private String displayName;
    private String hostname;
    private String description;
    private String icon;
    private int rdpPort;
    private boolean enabled;

    public MachineDto() {}

    public MachineDto(Machine machine) {
        this.id = machine.getId();
        this.displayName = machine.getDisplayName();
        this.hostname = machine.getHostname();
        this.description = machine.getDescription();
        this.icon = machine.getIcon();
        this.rdpPort = machine.getRdpPort();
        this.enabled = machine.isEnabled();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
