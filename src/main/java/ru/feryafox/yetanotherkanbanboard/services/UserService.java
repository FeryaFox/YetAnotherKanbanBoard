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
import ru.feryafox.yetanotherkanbanboard.models.UserInfoResponse;
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
        return login(loginRequest.getLogin(), loginRequest.getPassword());
    }

    public AuthResponse login(String login, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtils.generateToken(login);
        String refreshToken = jwtUtils.generateRefreshToken(login);

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

    public AuthResponse register(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getLogin())) {
            return null;
        }
        System.out.println(21432);
        User user = new User();
        user.setUsername(registrationRequest.getLogin());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setFirstName(registrationRequest.getName());
        user.setSurname(registrationRequest.getSurname());
        user.setMiddleName(registrationRequest.getMiddleName());
        user.setRoles("ROLE_USER");

        userRepository.save(user);

        return login(registrationRequest.getLogin(), registrationRequest.getPassword());
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

    public UserInfoResponse getUserInfo(String login) {
        User user = userRepository.findUserByUsername(login);

        UserInfoResponse.UserInfoResponseBuilder builder = UserInfoResponse.builder();

        builder.id(user.getId());
        builder.login(user.getUsername());
        builder.name(user.getFirstName());
        builder.surname(user.getSurname());
        builder.middleName(user.getMiddleName());

        return builder.build();
    }
}
