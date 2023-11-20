package com.khodedev.app.auth;

import com.khodedev.app.auth.dto.LoginDto;
import com.khodedev.app.auth.dto.LoginResDto;
import com.khodedev.app.common.annotations.Public;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Public
    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@RequestBody @Valid LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @Public
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResDto> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @Public
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String refreshToken) {
        return authService.logout(refreshToken);
    }
}
