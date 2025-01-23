package ru.feryafox.yetanotherkanbanboard.controllers.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.models.UserInfoResponse;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;
import ru.feryafox.yetanotherkanbanboard.services.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserInfo(userDetails.getUsername()));
    }


}
