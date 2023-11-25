package com.khodedev.app.common.services;

import com.khodedev.app.common.constants.Constants;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

    /**
     * Checks if the provided access token has the specified permission for the given resource and scope.
     *
     * @param accessToken The access token to be checked.
     * @param resource    The resource for which the permission is checked.
     * @param scope       The scope of the permission.
     * @return True if the permission is granted, false otherwise.
     */
    public boolean checkPermission(String accessToken, String resource, String scope) {
        try {
            // Build the Keycloak authorization server endpoint
            String endpoint = keycloakAuthorizationServer + "/realms/" + realm + "/protocol/openid-connect/token";

            // Construct the request body with necessary parameters
            var request = getStringHttpEntity(accessToken, resource, scope);

            // Make a POST request to the Keycloak authorization server
            var response = restTemplate.postForEntity(endpoint, request, String.class);

            // Check if the response status code is 2xx (successful)
            return Objects.requireNonNull(response.getStatusCode()).is2xxSuccessful();
        } catch (Exception e) {
            // Log any exceptions that occur during the authorization check
            log.severe(e.getMessage());
            return false;
        }
    }

    private HttpEntity<String> getStringHttpEntity(String accessToken, String resource, String scope) {
        String requestBody = "grant_type=urn:ietf:params:oauth:grant-type:uma-ticket" +
                "&audience=" + clientId +
                "&permission=" + resource + "#" + scope;

        // Set up headers for the request
        var headers = new HttpHeaders();
        headers.add(Constants.AUTHORIZATION, accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        // Create the HTTP request entity
        return new HttpEntity<>(requestBody, headers);
    }
}
