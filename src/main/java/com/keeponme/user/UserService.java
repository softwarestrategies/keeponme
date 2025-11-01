package com.keeponme.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User syncUserFromOidc(OidcUser oidcUser) {
        String keycloakId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String username = oidcUser.getPreferredUsername();

        Optional<User> existingUser = userRepository.findByKeycloakId(keycloakId);

        if (existingUser.isPresent()) {
            log.debug("User already exists: {}", username);
            return updateUser(existingUser.get(), oidcUser);
        } else {
            log.info("Creating new user: {}", username);
            return createUser(oidcUser);
        }
    }

    private User createUser(OidcUser oidcUser) {
        User user = User.builder()
                .keycloakId(oidcUser.getSubject())
                .username(oidcUser.getPreferredUsername())
                .email(oidcUser.getEmail())
                .firstName(oidcUser.getGivenName())
                .lastName(oidcUser.getFamilyName())
                .build();

        return userRepository.save(user);
    }

    private User updateUser(User user, OidcUser oidcUser) {
        boolean updated = false;

        if (!user.getEmail().equals(oidcUser.getEmail())) {
            user.setEmail(oidcUser.getEmail());
            updated = true;
        }

        if (oidcUser.getGivenName() != null && !oidcUser.getGivenName().equals(user.getFirstName())) {
            user.setFirstName(oidcUser.getGivenName());
            updated = true;
        }

        if (oidcUser.getFamilyName() != null && !oidcUser.getFamilyName().equals(user.getLastName())) {
            user.setLastName(oidcUser.getFamilyName());
            updated = true;
        }

        if (updated) {
            log.debug("Updating user information for: {}", user.getUsername());
            return userRepository.save(user);
        }

        return user;
    }

    public Optional<User> findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
