package com.khodedev.app.auth;

import com.khodedev.app.auth.dto.LoginDto;
import com.khodedev.app.auth.dto.LoginResDto;
import com.khodedev.app.common.exceptions.BadRequestException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Log
@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.authorization.server}")
    private String keycloakAuthorizationServer;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.redirect-uri}")
    private String redirectUri;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<LoginResDto> login(LoginDto loginDto) {
        log.info("Attempting login for username: " + loginDto.getUsername());
        try {
            validateLoginDto(loginDto);
            String url = buildEndpointUrl("token");
            String body = buildTokenRequestBody(loginDto);
            HttpHeaders headers = createHeaders();
            return sendTokenRequest(url, body, headers, "Invalid username or password");

        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e);
        }

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<LoginResDto> sendTokenRequest(String url, String body, HttpHeaders headers, String errorMessage) {
        try {
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            var loginResDto = LoginResDto.fromJson(response.getBody());
            return ResponseEntity.ok(loginResDto);
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e);
        } catch (IOException e) {
            throw new BadRequestException("Invalid response from authorization server");
        }

        return ResponseEntity.badRequest().build();
    }

    private void validateLoginDto(LoginDto loginDto) {
        if (loginDto == null || loginDto.getUsername() == null || loginDto.getPassword() == null) {
            throw new IllegalArgumentException("LoginDto must not be null, and username and password must be provided.");
        }
    }

    private String buildEndpointUrl(String endpoint) {
        return UriComponentsBuilder
                .fromUriString(keycloakAuthorizationServer)
                .pathSegment("realms", realm, "protocol", "openid-connect", endpoint)
                .build()
                .toUriString();
    }

    private String buildTokenRequestBody(LoginDto loginDto) {
        return "grant_type=password&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&username=" + loginDto.getUsername() +
                "&password=" + loginDto.getPassword();
    }

    private void handleHttpClientErrorException(HttpClientErrorException e) {
        if (e.getStatusCode().is4xxClientError()) {
            throw new BadRequestException("Invalid username or password");
        } else {
            throw new BadRequestException("Invalid response from authorization server");
        }
    }

    public ResponseEntity<LoginResDto> refreshToken(String refreshToken) {
        try {
            String refreshTokenWithoutBearer = refreshToken.substring(7); // remove "Bearer " from the token
            validateRefreshToken(refreshTokenWithoutBearer);
            String url = buildEndpointUrl("token");
            String body = buildRefreshTokenRequestBody(refreshTokenWithoutBearer);
            HttpHeaders headers = createHeaders();
            return sendTokenRequest(url, body, headers, "Invalid refresh token");

        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e);
        }

        return ResponseEntity.badRequest().build();
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("RefreshToken must not be null");
        }
    }

    private String buildRefreshTokenRequestBody(String refreshToken) {
        return "grant_type=refresh_token&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + refreshToken;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    // logout
    public ResponseEntity<String> logout(String refreshToken) {
        try {
            String refreshTokenWithoutBearer = refreshToken.substring(7); // remove "Bearer " from the token
            validateRefreshToken(refreshTokenWithoutBearer);
            String url = buildEndpointUrl("logout");
            String body = buildLogoutRequestBody(refreshTokenWithoutBearer);
            HttpHeaders headers = createHeaders();
            return sendLogoutRequest(url, body, headers, "Invalid refresh token");

        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e);
        }

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<String> sendLogoutRequest(String url, String body, HttpHeaders headers, String errorMessage) {
        try {
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e);
        }

        return ResponseEntity.badRequest().build();
    }

    private String buildLogoutRequestBody(String refreshToken) {
        return "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + refreshToken;
    }

    // login with keycloak ui
    public String getAuthorizationUrl() {
        return UriComponentsBuilder
                .fromHttpUrl(keycloakAuthorizationServer)
                .pathSegment("realms", realm, "protocol", "openid-connect", "auth")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")
                .queryParam("redirect_uri", redirectUri)
                .build()
                .toUriString();
    }

    // exchange code for token
    public String exchangeCodeForToken(String code) {
        String url = UriComponentsBuilder
                .fromHttpUrl(keycloakAuthorizationServer)
                .pathSegment("realms", realm, "protocol", "openid-connect", "token")
                .build()
                .toUriString();

        var map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", code);
        map.add("scope", "openid email profile");
        map.add("redirect_uri", redirectUri);
        var response = restTemplate.postForEntity(url, map, String.class);
        return response.getBody();
    }

}
