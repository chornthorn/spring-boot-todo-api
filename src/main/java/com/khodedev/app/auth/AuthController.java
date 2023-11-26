package com.khodedev.app.auth;

import com.khodedev.app.auth.dto.LoginDto;
import com.khodedev.app.auth.dto.LoginResDto;
import com.khodedev.app.common.annotations.Public;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Public
    @PostMapping("/api/login")
    public ResponseEntity<LoginResDto> login(@RequestBody @Valid LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @Public
    @PostMapping("/api/refresh-token")
    public ResponseEntity<LoginResDto> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @Public
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String refreshToken) {
        return authService.logout(refreshToken);
    }

    @Public
    @GetMapping("/login")
    public void login(HttpServletResponse response){
        try {
            response.sendRedirect(authService.getAuthorizationUrl());
        } catch (Exception e) {
            log.info("Error during login: " + e);
            throw new RuntimeException("Error during login", e);
        }
    }

    @Public
    @GetMapping("/callback")
    @ResponseBody
    public ResponseEntity<String> handleCallback(@RequestParam("code") String code) {
        String accessToken = authService.exchangeCodeForToken(code);
        return ResponseEntity.ok(accessToken);
    }
}
