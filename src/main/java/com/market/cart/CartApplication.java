package com.market.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
@EnableConfigurationProperties(UploadProperties.class)
public class CartApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartApplication.class, args);
	}

//	private final RoleRepository roleRepository;
//
//	@Bean
//	public CommandLineRunner createDefaultAdmin(UserRepository userRepository,
//												RoleRepository roleRepository,
//												PasswordEncoder passwordEncoder) {
//		return args -> {
//			String adminUsername = "admin";
//			if (userRepository.findByUsername(adminUsername).isPresent()) {
//				return;
//			}
//
//			// Ensure role exists
//			Role adminRole = roleRepository.findByName("ROLE_ADMIN")
//					.orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
//
//			User admin = new User();
//			admin.setUsername(adminUsername);
//			admin.setEmail("admin@example.com");
//			admin.setPassword(passwordEncoder.encode("admin")); // change in prod
//			admin.setRole(adminRole);
//			// set other fields as required
//			userRepository.save(admin);
//			System.out.println("Default admin user created: admin/admin");
//		};
//	}
}




