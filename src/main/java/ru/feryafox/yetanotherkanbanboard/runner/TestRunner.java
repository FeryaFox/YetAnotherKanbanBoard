package ru.feryafox.yetanotherkanbanboard.runner;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.feryafox.yetanotherkanbanboard.components.auth.JwtUtils;

import java.util.Set;

@Component
public class TestRunner implements CommandLineRunner {

    private final JwtUtils jwtUtils;

    public TestRunner(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void run(String... args) throws Exception {
        var a = jwtUtils.generateRefreshToken("test", Set.of("user1", "user2"));
        System.out.println(a);
        System.out.println(jwtUtils.getUserAgentsFromToken(a));
    }
}
