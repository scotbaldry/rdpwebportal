package com.rdpportal.dto;

public class CreateMachineRequest {
    private String displayName;
    private String hostname;
    private String description;
    private String icon;
    private int rdpPort = 3389;
    private boolean enabled = true;

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
