package com.market.cart;

import com.market.cart.entity.role.Role;
import com.market.cart.entity.role.RoleRepository;
import com.market.cart.entity.user.User;
import com.market.cart.entity.user.UserRepository;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/// This class creates an admin in the system everytime the server boots (if username admin does not exist)

@Component
@RequiredArgsConstructor
public class AdminInit {

    @Value("${app.admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            Role role = roleRepository.findById(1L).orElseThrow(() -> new CustomTargetNotFoundException("No role found with role id: 1", "adminInitializer"));
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(role);
            admin.setEmail("admin@mail.gg");

            userRepository.save(admin);
        }
    }


}
