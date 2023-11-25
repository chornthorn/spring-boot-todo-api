package com.khodedev.app.common.config;

import com.khodedev.app.common.constants.Constants;
import com.khodedev.app.common.filters.JwtAuthenticationFilter;
import com.khodedev.app.common.filters.PreAuthzFilter;
import com.khodedev.app.common.filters.PublicAccessFilter;
import com.khodedev.app.common.services.KeycloakAuthorizationService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Log
@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final RequestMappingHandlerMapping handlerMapping;
    private final KeycloakAuthorizationService keycloakAuthorizationService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // disable csrf cuz we're not using cookies
        http.csrf(AbstractHttpConfigurer::disable);

        // authorization
        http
                .addFilterBefore(new PublicAccessFilter(handlerMapping), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new PreAuthzFilter(handlerMapping), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        c -> {
                            c.requestMatchers(Constants.ERROR_ROUTE).permitAll();
                            c.requestMatchers("/**").access(authorizationDecisionManager());
                            c.anyRequest().authenticated();
                        }
                );

        return http.build();
    }

    /**
     * Creates an AuthorizationManager for handling authorization decisions based on the provided context.
     *
     * @return An AuthorizationManager instance.
     */
    private AuthorizationManager<RequestAuthorizationContext> authorizationDecisionManager() {
        return (auth, ctx) -> {
            log.info("Authorization Decision Manager is running...");

            // Check if the request is marked as public, allowing unrestricted access
            var isPublic = ctx.getRequest().getAttribute(Constants.IS_PUBLIC);
            if (isPublic != null && (boolean) isPublic) {
                return new AuthorizationDecision(true);
            }

            // Check if no Authorization header is present in the request
            var authorizationHeader = ctx.getRequest().getHeader(Constants.AUTHORIZATION);
            if (authorizationHeader == null) {
                // Set status code to 401 (Unauthorized)
                ctx.getRequest().setAttribute(Constants.STATUS_CODE, 401);
                return new AuthorizationDecision(false); // If false, it will result in a 403 (Forbidden)
            }

            // Extract resource, scope, and access token from the request attributes and headers
            var resource = (String) ctx.getRequest().getAttribute(Constants.RESOURCE);
            var scope = (String) ctx.getRequest().getAttribute(Constants.SCOPE);
            var accessToken = ctx.getRequest().getHeader(Constants.AUTHORIZATION);

            // Check if resource or scope is missing
            if (resource == null || scope == null) {
                // Set status code to 403 (Forbidden)
                ctx.getRequest().setAttribute(Constants.STATUS_CODE, 403);
                return new AuthorizationDecision(false);
            }

            // Check authorization using the KeycloakAuthorizationService
            var hasAuthority = keycloakAuthorizationService.checkPermission(accessToken, resource, scope);
            return new AuthorizationDecision(hasAuthority);
        };
    }
}
