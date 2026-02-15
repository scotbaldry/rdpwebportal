package com.rdpportal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String hostname;

    private String description;

    private String icon;

    @Column(nullable = false)
    private int rdpPort = 3389;

    @Column(nullable = false)
    private boolean enabled = true;

    public Machine() {}

    public Machine(String displayName, String hostname) {
        this.displayName = displayName;
        this.hostname = hostname;
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
