package com.khodedev.app.common.filters;

import com.khodedev.app.common.annotations.KeycloakAuthorz;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;

@Log
public class KeycloakAuthorzFilter extends OncePerRequestFilter {

    private final RequestMappingHandlerMapping handlerMapping;

    public KeycloakAuthorzFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Check if the endpoint is marked as public, allowing unrestricted access
            var isPublic = request.getAttribute("isPublic");
            if (isPublic != null && (boolean) isPublic) {
                filterChain.doFilter(request, response);
                return;
            }

            // Attempt to get the handler execution chain for the request
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);

            if (handlerExecutionChain != null) {
                // Extract handler method and check for the KeycloakAuthorz annotation
                HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
                KeycloakAuthorz keycloakAuthorz = findKeycloakAuthorzAnnotation(handlerMethod);

                if (keycloakAuthorz != null) {
                    // Check if Authorization header is present
                    String accessToken = request.getHeader("Authorization");
                    if (accessToken == null) {
                        // Set response status to 401 (Unauthorized) if Authorization header is missing
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    } else {
                        // Set resource and scope attributes based on KeycloakAuthorz annotation
                        request.setAttribute("resource", keycloakAuthorz.resource());
                        request.setAttribute("scope", keycloakAuthorz.scope().getScope());
                    }
                }
            }

            // Continue with the filter chain
            filterChain.doFilter(request, response);
            log.info("KeycloakAuthorzFilter is running...");

        } catch (Exception e) {
            // Handle exceptions that may occur while processing the request
            throw new RuntimeException(e);
        }
    }

    private KeycloakAuthorz findKeycloakAuthorzAnnotation(HandlerMethod handlerMethod) {
        // Find KeycloakAuthorz annotation on the method or the class level
        KeycloakAuthorz keycloakAuthorz = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), KeycloakAuthorz.class);
        if (keycloakAuthorz == null) {
            keycloakAuthorz = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), KeycloakAuthorz.class);
        }
        return keycloakAuthorz;
    }
}
