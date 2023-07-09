package com.mtd.crypto.core.user.rest;

import com.mtd.crypto.core.security.configuration.UserPrincipal;
import com.mtd.crypto.core.security.exception.AuthenticationException;
import com.mtd.crypto.core.security.service.JwtTokenService;
import com.mtd.crypto.core.user.data.entity.RefreshToken;
import com.mtd.crypto.core.user.data.request.LoginRequest;
import com.mtd.crypto.core.user.data.request.RefreshTokenRequest;
import com.mtd.crypto.core.user.data.response.LoginResponse;
import com.mtd.crypto.core.user.service.RefreshTokenService;
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
//@CrossOrigin(origins = "http://192.168.4.22:8082")

public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;


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

        RefreshToken refreshToken = refreshTokenService.getToken(principal.getUser());


        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(principal.getUser().getId())
                .email(principal.getUsername())
                .build();
    }


    @PostMapping("refresh-token")
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest)
            throws AuthenticationException {
        RefreshToken refreshToken = refreshTokenService.getByToken(
                        refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        String accessToken = jwtTokenService.createTokenFromUserId(refreshToken.getUser().getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .userId(refreshToken.getUser().getId())
                .email(refreshToken.getUser().getEmail())
                .build();
    }

}
