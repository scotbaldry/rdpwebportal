package com.rdpportal.model;

import com.rdpportal.config.EncryptedStringConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "user_machine_assignments")
public class UserMachineAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    private String rdpUsername;

    private String rdpDomain;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(length = 1024)
    private String rdpPassword;

    private boolean useMultimon = false;

    public UserMachineAssignment() {}

    public UserMachineAssignment(User user, Machine machine) {
        this.user = user;
        this.machine = machine;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Machine getMachine() { return machine; }
    public void setMachine(Machine machine) { this.machine = machine; }

    public String getRdpUsername() { return rdpUsername; }
    public void setRdpUsername(String rdpUsername) { this.rdpUsername = rdpUsername; }

    public String getRdpDomain() { return rdpDomain; }
    public void setRdpDomain(String rdpDomain) { this.rdpDomain = rdpDomain; }

    public String getRdpPassword() { return rdpPassword; }
    public void setRdpPassword(String rdpPassword) { this.rdpPassword = rdpPassword; }

    public boolean isUseMultimon() { return useMultimon; }
    public void setUseMultimon(boolean useMultimon) { this.useMultimon = useMultimon; }
}
