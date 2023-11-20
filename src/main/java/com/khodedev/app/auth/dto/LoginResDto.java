package com.khodedev.app.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResDto {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_expires_in")
    private int refreshExpiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonIgnore
    @JsonProperty("not-before-policy")
    private int notBeforePolicy;

    @JsonIgnore
    @JsonProperty("session_state")
    private String sessionState;

    @JsonIgnore
    @JsonProperty("scope")
    private String scope;

    public static LoginResDto fromJson(String body) throws IOException {
        var objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, LoginResDto.class);
    }
}
