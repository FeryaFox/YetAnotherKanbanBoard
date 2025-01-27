package ru.feryafox.yetanotherkanbanboard.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.components.utils.IpGetter;
import ru.feryafox.yetanotherkanbanboard.components.auth.JwtUtils;
import ru.feryafox.yetanotherkanbanboard.configs.AppConfig;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.entities.RefreshToken;
import ru.feryafox.yetanotherkanbanboard.exceptions.auth.MissingUserAgentException;
import ru.feryafox.yetanotherkanbanboard.models.UserInfoResponse;
import ru.feryafox.yetanotherkanbanboard.repositories.CardRepository;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.models.auth.AuthResponse;
import ru.feryafox.yetanotherkanbanboard.models.auth.LoginRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RefreshRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RegistrationRequest;
import ru.feryafox.yetanotherkanbanboard.repositories.BoardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.RefreshTokenRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private final IpGetter ipGetter;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppConfig appConfig;
    private final DaoService daoService;

    public UserService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, UserRepository userRepository, BoardRepository boardRepository, CardRepository cardRepository, IpGetter ipGetter, RefreshTokenRepository refreshTokenRepository, AppConfig appConfig, DaoService daoService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
        this.ipGetter = ipGetter;
        this.refreshTokenRepository = refreshTokenRepository;
        this.appConfig = appConfig;
        this.daoService = daoService;
    }

    public AuthResponse login(LoginRequest loginRequest, String userAgent, HttpServletRequest request) throws MissingUserAgentException {

        return login(loginRequest.getLogin(), loginRequest.getPassword(), userAgent);
    }

    public AuthResponse login(String login, String password, String userAgent) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        RefreshToken refreshToken = getRefreshToken(login);
        // Если токена нет, то создадим его

        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
        }

        String jwtRefreshToken = jwtUtils.generateRefreshToken(login, Set.of());

        refreshToken.setToken(jwtRefreshToken);

        refreshTokenRepository.save(refreshToken);
//        if (refreshToken == null) {
//            String jwtRefreshToken = jwtUtils.generateRefreshToken(login, Set.of());
//            RefreshToken.RefreshTokenBuilder refreshTokenBuilder = RefreshToken.builder();
//
//            refreshTokenBuilder.user(user);
//            refreshTokenBuilder.token(jwtRefreshToken);
//            refreshToken = refreshTokenRepository.save(refreshTokenBuilder.build());
//        }

//        String jwtRefreshToken = refreshToken.getToken();

//        try {
//            //
//            if (jwtUtils.validateToken(jwtRefreshToken)) {
//                var userAgents = jwtUtils.getUserAgentsFromToken(jwtRefreshToken);
//                if (userAgents.size() > appConfig.getMaxSessions()) {
//                    userAgents = Set.of(userAgent);
//                } else {
//                    userAgents.add(userAgent);
//                }
//
//                jwtRefreshToken = jwtUtils.generateRefreshToken(login, userAgents);
//            }
//        }

        String jwtToken = jwtUtils.generateToken(login);
//        String refreshToken = jwtUtils.generateRefreshToken(login);

        return new AuthResponse(jwtToken);
    }

    @Transactional
    public AuthResponse refresh(String authorization, String userAgent, HttpServletRequest request) throws MissingUserAgentException {
        checkUserAgent(userAgent, request);
        String username = jwtUtils.getUsernameFromExpiredToken(jwtUtils.getTokenFromHeader(authorization));

        RefreshToken refreshToken = getRefreshToken(username);

        if (jwtUtils.validateToken(refreshToken.getToken())){
            refreshToken.setToken(jwtUtils.generateRefreshToken(username, Set.of()));
            refreshTokenRepository.save(refreshToken);
            return new AuthResponse(jwtUtils.generateToken(username));
        }
        return null;
//        String refreshToken = refreshRequest.getRefreshToken();
//        if (jwtUtils.validateToken(refreshToken)) {
//            String username = jwtUtils.getUsernameFromToken(refreshToken);
//            String newToken = jwtUtils.generateToken(username);
//            return new AuthResponse(refreshToken);
//        }
//        return null;
    }

    public AuthResponse register(RegistrationRequest registrationRequest, String userAgent, HttpServletRequest request) throws MissingUserAgentException {

        checkUserAgent(userAgent, request);

        if (userRepository.existsByUsername(registrationRequest.getLogin())) {
            return null;
        }
        User user = new User();
        user.setUsername(registrationRequest.getLogin());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setFirstName(registrationRequest.getName());
        user.setSurname(registrationRequest.getSurname());
        user.setMiddleName(registrationRequest.getMiddleName());
        user.setRoles("ROLE_USER");

        userRepository.save(user);

        return login(registrationRequest.getLogin(), registrationRequest.getPassword(), userAgent);
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

    private RefreshToken getRefreshToken(String username) {
        return refreshTokenRepository.findByUser_Username(username).orElse(null);
    }

    private void checkUserAgent(String userAgent, HttpServletRequest request) throws MissingUserAgentException {
        if (userAgent == null || userAgent.isEmpty()) {
            String ip = ipGetter.getClientIp(request);
            throw new MissingUserAgentException(ip);
        }
    }
}
