package com.khodedev.app.common.filters;

import com.khodedev.app.common.constants.Constants;
import com.khodedev.app.common.exceptions.UnauthorizedException;
import com.khodedev.app.common.services.JwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log
@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Check if the endpoint is marked as public, allowing unrestricted access
            var isPublic = request.getAttribute(Constants.IS_PUBLIC);
            if (isPublic != null && (boolean) isPublic) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract and validate the Authorization header
            final String authorizationHeader = request.getHeader(Constants.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Invalid token");
            }

            // Extract the token and validate it using JwtTokenValidator
            final String token = authorizationHeader.substring(7);
            if (!jwtTokenValidator.validateToken(token)) {
                throw new UnauthorizedException("Invalid token");
            }

            // Continue with the filter chain if the token is valid
            filterChain.doFilter(request, response);
            log.info("JwtAuthenticationFilter is running...");
        } catch (UnauthorizedException e) {
            // Handle UnauthorizedException by setting status code and continuing with the filter chain
            response.setStatus(401);
            request.setAttribute(Constants.STATUS_CODE, 401);
            filterChain.doFilter(request, response);
        }
    }
}
