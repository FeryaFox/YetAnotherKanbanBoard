package ru.feryafox.yetanotherkanbanboard.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.components.auth.JwtUtils;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.models.auth.AuthResponse;
import ru.feryafox.yetanotherkanbanboard.models.auth.LoginRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RefreshRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RegistrationRequest;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public AuthResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtils.generateToken(loginRequest.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getUsername());

        return new AuthResponse(jwtToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        if (jwtUtils.validateToken(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            String newToken = jwtUtils.generateToken(username);
            return new AuthResponse(refreshToken, newToken);
        }
        return null;
    }

    public boolean register(RegistrationRequest registrationRequest) {

        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            return false;
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setFirstName(registrationRequest.getFirstName());
        user.setSurname(registrationRequest.getSurname());
        user.setMiddleName(registrationRequest.getMiddleName());
        user.setRoles("ROLE_USER");

        userRepository.save(user);

        return true;
    }
}
