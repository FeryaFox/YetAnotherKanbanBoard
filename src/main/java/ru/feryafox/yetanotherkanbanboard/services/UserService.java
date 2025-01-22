package ru.feryafox.yetanotherkanbanboard.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.components.auth.JwtUtils;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.entities.Card;
import ru.feryafox.yetanotherkanbanboard.repositories.CardRepository;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.models.auth.AuthResponse;
import ru.feryafox.yetanotherkanbanboard.models.auth.LoginRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RefreshRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RegistrationRequest;
import ru.feryafox.yetanotherkanbanboard.repositories.BoardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;

    public UserService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, UserRepository userRepository, BoardRepository boardRepository, CardRepository cardRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
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


    public boolean isBoardOwner(Long boardId, UserDetails userDetails) {
        return isBoardOwner(boardId, userDetails.getUsername());
    }

    public boolean isBoardOwner(Long boardId, String username) {

        User user = userRepository.findUserByUsername(username);

        Board board = boardRepository.findBoardWithAccess(boardId, user.getId()).orElse(null);

        return board != null;
    }

    public boolean isCardAccessible(Long cardId, String username) {
        User user = userRepository.getUserByCardId(username, cardId).orElse(null);

        return user != null && user.getUsername().equals(username);
    }


    public boolean isColumnAccessible(Long columnId, String username) {
        User user = userRepository.getUserByColumnId(username, columnId).orElse(null);

        return user != null && user.getUsername().equals(username);
    }
}
