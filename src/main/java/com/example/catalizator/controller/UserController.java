package com.example.catalizator.controller;

import com.example.catalizator.config.JwtUtil;
import com.example.catalizator.domain.User;
import com.example.catalizator.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final static ResponseEntity<Object> UNAUTHORIZED =
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity> login(ServerWebExchange swe){
        return swe.getFormData().flatMap(credentials ->
                userService.findByUsername(credentials.getFirst("username"))
                .cast(User.class)
                .map(userDetails ->
                                Objects.equals(credentials.getFirst("password"),
                                        userDetails.getPassword()
                                )
                        ? ResponseEntity.ok(jwtUtil.generateToken(userDetails))

                                        : UNAUTHORIZED
                        )
                .defaultIfEmpty(UNAUTHORIZED)
                );
    }

}
