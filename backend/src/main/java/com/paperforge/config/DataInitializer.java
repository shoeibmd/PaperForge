package com.paperforge.config;

import com.paperforge.model.Role;
import com.paperforge.model.User;
import com.paperforge.repository.RoleRepository;
import com.paperforge.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${PAPERFORGE_ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${PAPERFORGE_ADMIN_PASSWORD:PaperForgeAdminPass123!}")
    private String adminPassword;

    @Value("${PAPERFORGE_ADMIN_EMAIL:admin@paperforge.local}")
    private String adminEmail;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User(adminUsername, adminEmail, passwordEncoder.encode(adminPassword));
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            logger.info("Created default PaperForge admin account: {}", adminUsername);
        }
    }
}
