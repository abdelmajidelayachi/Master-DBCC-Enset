package dev.elayachi.mbank.services;

import dev.elayachi.mbank.entities.AppUser;
import dev.elayachi.mbank.exceptions.InvalidPasswordException;
import dev.elayachi.mbank.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser addUser(String username, String rawPassword, String roles) {
        AppUser user = new AppUser(null, username, passwordEncoder.encode(rawPassword), roles);
        return appUserRepository.save(user);
    }

    public void changePassword(String username, String oldPassword, String newPassword)
            throws InvalidPasswordException {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Ancien mot de passe incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(user);
    }
}
