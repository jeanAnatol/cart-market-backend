package com.market.cart;

import com.market.cart.entity.role.Role;
import com.market.cart.entity.role.RoleRepository;
import com.market.cart.entity.user.User;
import com.market.cart.entity.user.UserRepository;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes the default administrator account at application startup.
 *
 * <p>
 * This component runs once after the Spring context is fully initialized.
 * It ensures that:
 * </p>
 * <ul>
 *     <li>An {@code ADMIN} role exists</li>
 *     <li>An administrator user with username {@code admin} exists</li>
 * </ul>
 *
 * <p>
 * The operation is <b>idempotent</b>: if the role or user already exists,
 * no duplicate records are created.
 * </p>
 *
 * <p>
 * The administrator password is loaded from application.properties
 * ({@code app.admin.password}) and securely hashed using the configured
 * {@link PasswordEncoder}.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInit {

    /**
     * Initial administrator password loaded from application configuration.
     */
    @Value("${app.admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * Executes admin initialization logic after application startup.
     *
     * <p>
     * This method:
     * </p>
     * <ol>
     *     <li>Ensures the {@code ADMIN} role exists</li>
     *     <li>Creates an {@code admin} user if it does not already exist</li>
     * </ol>
     */
    @PostConstruct
    public void init() {
        log.info("Running admin initialization");

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    log.warn("ADMIN role not found — creating default ADMIN role");
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });
        if (!userRepository.existsByUsername("admin")) {
            log.warn("Admin user not found — creating default admin account");
            User admin = new User();
            admin.setUsername("admin");
            if (adminPassword == null || adminPassword.isBlank()) {
                log.error("app.admin.password is EMPTY. Defaulting to PASSWORD 'admin' for safety.");
                adminPassword = "admin";
            }
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail("admin@mail.gg");
            admin.setRole(adminRole);

            userRepository.save(admin);
            log.info("Default admin account created successfully");
        } else {
            log.info("Admin user already exists — skipping creation");
        }
    }
}
