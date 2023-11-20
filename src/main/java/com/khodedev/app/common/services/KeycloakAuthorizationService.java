package com.khodedev.app.common.services;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Log
@Service
public class KeycloakAuthorizationService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.authorization.server}")
    private String keycloakAuthorizationServer;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    public KeycloakAuthorizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean checkPermission(String accessToken, String resource, String scope) {
        try {
            String endpoint = keycloakAuthorizationServer + "/realms/" + realm + "/protocol/openid-connect/token";
            String requestBody = "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" +
                    "&audience=" + clientId +
                    "&permission=" + resource + "#" + scope;

            var headers = new org.springframework.http.HttpHeaders();
            headers.add("Authorization",accessToken);
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            var request = new org.springframework.http.HttpEntity<>(requestBody, headers);
            var response = restTemplate.postForEntity(endpoint, request, String.class);

            // just get response status code 200
            return Objects.requireNonNull(response.getStatusCode()).is2xxSuccessful();
        } catch (Exception e) {
            log.severe(e.getMessage());
            return false;
        }
    }
}
