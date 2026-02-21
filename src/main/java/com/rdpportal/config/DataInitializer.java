package com.rdpportal.config;

import com.rdpportal.model.AppSettings;
import com.rdpportal.model.Role;
import com.rdpportal.model.User;
import com.rdpportal.repository.AppSettingsRepository;
import com.rdpportal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final AppSettingsRepository appSettingsRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JdbcTemplate jdbcTemplate, AppSettingsRepository appSettingsRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.appSettingsRepository = appSettingsRepository;
    }

    @Override
    public void run(String... args) {
        if (!appSettingsRepository.existsById(1L)) {
            appSettingsRepository.save(new AppSettings());
        }

        if (userRepository.count() == 0) {
            User admin = new User(
                "admin",
                passwordEncoder.encode("admin"),
                "Administrator",
                Role.ADMIN
            );
            userRepository.save(admin);
            log.info("Created default admin user (username: admin, password: admin)");
        }

        dropUniqueConstraintIfExists();
    }

    private void dropUniqueConstraintIfExists() {
        try {
            var constraints = jdbcTemplate.queryForList(
                "SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS " +
                "WHERE TABLE_NAME = 'USER_MACHINE_ASSIGNMENTS' AND CONSTRAINT_TYPE = 'UNIQUE'"
            );
            for (var row : constraints) {
                String name = (String) row.get("CONSTRAINT_NAME");
                jdbcTemplate.execute("ALTER TABLE USER_MACHINE_ASSIGNMENTS DROP CONSTRAINT " + name);
                log.info("Dropped unique constraint {} from USER_MACHINE_ASSIGNMENTS", name);
            }
        } catch (Exception e) {
            log.debug("No unique constraint to drop on USER_MACHINE_ASSIGNMENTS: {}", e.getMessage());
        }
    }
}
