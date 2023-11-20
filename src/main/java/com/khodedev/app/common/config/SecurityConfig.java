package com.khodedev.app.common.config;


import com.khodedev.app.common.filters.JwtAuthenticationFilter;
import com.khodedev.app.common.filters.KeycloakAuthorzFilter;
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
                .addFilterBefore(new KeycloakAuthorzFilter(handlerMapping), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        c -> {
                            c.requestMatchers("/users").permitAll();
                            c.requestMatchers("/error").permitAll();
                            c.requestMatchers("/**").access(authorizationDecisionManager());
                            c.anyRequest().authenticated();
                        }
                );

        return http.build();
    }

    private AuthorizationManager<RequestAuthorizationContext> authorizationDecisionManager() {
        return (auth, ctx) -> {
            var isPublic = ctx.getRequest().getAttribute("isPublic");

            if (isPublic != null && (boolean) isPublic) {
                return new AuthorizationDecision(true);
            }

            var resource = (String) ctx.getRequest().getAttribute("resource");
            var scope = (String) ctx.getRequest().getAttribute("scope");
            var accessToken = ctx.getRequest().getHeader("Authorization");

            var hasAuthority = keycloakAuthorizationService.checkPermission(accessToken, resource, scope);
            return new AuthorizationDecision(hasAuthority);
        };
    }
}
