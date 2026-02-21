package com.rdpportal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_settings")
public class AppSettings {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private String siteName = "RDP Web Portal";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
}
