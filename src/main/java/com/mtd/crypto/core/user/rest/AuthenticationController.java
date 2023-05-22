package com.mtd.crypto.core.user.rest;

import com.mtd.crypto.core.security.configuration.UserPrincipal;
import com.mtd.crypto.core.security.service.JwtTokenService;
import com.mtd.crypto.core.user.data.request.LoginRequest;
import com.mtd.crypto.core.user.data.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("authentication")
@RequiredArgsConstructor

public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;


    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtTokenService.createTokenFromAuthentication(
                authentication);


        return LoginResponse.builder()
                .accessToken(accessToken)
                .userId(principal.getUser().getId())
                .email(principal.getUsername())
                .build();
    }

}
