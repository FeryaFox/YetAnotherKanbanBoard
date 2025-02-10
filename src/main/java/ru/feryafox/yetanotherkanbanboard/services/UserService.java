package ru.feryafox.yetanotherkanbanboard.services;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

import java.util.HashSet;
import java.util.List;
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

//        if (refreshToken == null) {
//            refreshToken = new RefreshToken();
//            refreshToken.setUser(user);
//        }

//        String jwtRefreshToken = jwtUtils.generateRefreshToken(login, Set.of());
//        refreshToken.setToken(jwtRefreshToken);
//
//        refreshTokenRepository.save(refreshToken);

        if (refreshToken == null) {
            // Сгенерируем Refresh Токен
            String jwtRefreshToken = jwtUtils.generateRefreshToken(login, Set.of());
            RefreshToken.RefreshTokenBuilder refreshTokenBuilder = RefreshToken.builder();

            refreshTokenBuilder.user(user);
            refreshTokenBuilder.token(jwtRefreshToken);
            // Сохраним Refresh токен в БД
            refreshToken = refreshTokenRepository.save(refreshTokenBuilder.build());
        }

        String jwtRefreshToken = refreshToken.getToken();

        try {
            // проверяем валидность Refresh токена
            if (jwtUtils.validateToken(jwtRefreshToken)) {
                // если он валидный, то получаем UserAgents, которые в нем
                var userAgents = jwtUtils.getUserAgentsFromToken(jwtRefreshToken);
                System.out.println(userAgents);

                if (userAgents.size() > appConfig.getMaxSession()) {
                    // Если их больше, чем максимальное количество, то сбрасываем все
                    userAgents = new HashSet<>(Set.of(userAgent));
                } else {
                    userAgents = new HashSet<>(userAgents);
                    userAgents.add(userAgent);
                }
                // Генерируем новый токен
                jwtRefreshToken = jwtUtils.generateRefreshToken(login, userAgents);
            }
        }
        catch (ExpiredJwtException e){
            jwtRefreshToken = jwtUtils.generateRefreshToken(login, Set.of(userAgent));
        }

        String jwtToken = jwtUtils.generateToken(login);
//        String refreshToken = jwtUtils.generateRefreshToken(login);


        refreshToken.setToken(jwtRefreshToken);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(jwtToken);
    }

    @Transactional
    public AuthResponse refresh(String authorization, String userAgent, HttpServletRequest request) throws MissingUserAgentException {
        checkUserAgent(userAgent, request); // Проверяем, что userAgent передан

        // Извлекаем токен из заголовка и получаем username
        String expiredToken = jwtUtils.getTokenFromHeader(authorization);
        String username;
        try {
            username = jwtUtils.getUsernameFromExpiredToken(expiredToken);
        } catch (JwtException e) {
            log.warn("Invalid or malformed expired token: {}", e.getMessage());
            return null;
        }

        // Получаем RefreshToken из базы
        RefreshToken refreshToken = getRefreshToken(username);

        if (refreshToken == null) {
            log.warn("Refresh token not found for user: {}", username);
            return null; // Если refresh-токен отсутствует, возвращаем null
        }

        Set<String> userAgents;
        try {
            // Ключевой момент: создаем изменяемый HashSet вместо неизменяемого Set.copyOf
            userAgents = new HashSet<>(jwtUtils.getUserAgentsFromToken(refreshToken.getToken()));
        } catch (JwtException e) {
            log.warn("Invalid refresh token for user {}: {}", username, e.getMessage());
            return null;
        }

        // Проверяем, содержится ли текущий User-Agent в refreshToken
        if (!userAgents.contains(userAgent)) {
            log.warn("Unauthorized User-Agent access attempt for user: {}", username);
            throw new MissingUserAgentException("User-Agent не найден в refresh токене");
        }

        // Проверяем валидность refresh токена
        try {
            jwtUtils.validateToken(refreshToken.getToken());
        } catch (ExpiredJwtException e) {
            log.info("Refresh token expired for user: {}, requiring re-authentication", username);
            return null; // Если refresh-токен просрочен, требуем повторного входа
        }

        // Генерируем новый Access-токен
        String newAccessToken = jwtUtils.generateToken(username);

        // Добавляем новый userAgent (если он новый)
        userAgents.add(userAgent);

        // Создаем новый Refresh-токен
        String newRefreshToken = jwtUtils.generateRefreshToken(username, userAgents);

        // Обновляем Refresh-токен в БД
        refreshToken.setToken(newRefreshToken);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(newAccessToken);
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
