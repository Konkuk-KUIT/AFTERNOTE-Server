package com.example.afternote.domain.admin.config;

import com.example.afternote.domain.user.model.AuthProvider;
import com.example.afternote.domain.user.model.User;
import com.example.afternote.domain.user.model.UserRole;
import com.example.afternote.domain.user.model.UserStatus;
import com.example.afternote.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.info("Admin account environment variables (ADMIN_EMAIL, ADMIN_PASSWORD) not set. Skipping admin account initialization.");
            return;
        }

        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                existingUser -> {
                    if (existingUser.getRole() != UserRole.ADMIN) {
                        existingUser.updateRole(UserRole.ADMIN);
                        log.info("Existing user {} role updated to ADMIN.", adminEmail);
                    } else {
                        log.info("Admin account already exists: {}", adminEmail);
                    }
                },
                () -> {
                    User admin = User.builder()
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword))
                            .name("Admin")
                            .status(UserStatus.ACTIVE)
                            .provider(AuthProvider.LOCAL)
                            .build();
                    // Need to set role to ADMIN - add a method to User for this
                    admin.updateRole(UserRole.ADMIN);
                    userRepository.save(admin);
                    log.info("Admin account created: {}", adminEmail);
                }
        );
    }
}
