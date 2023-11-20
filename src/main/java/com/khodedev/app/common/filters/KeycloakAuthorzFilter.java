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

        var isPublic = request.getAttribute("isPublic");

        if (isPublic != null && (boolean) isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        HandlerExecutionChain handlerExecutionChain = null;
        try {
            handlerExecutionChain = handlerMapping.getHandler(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (handlerExecutionChain != null) {
            HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
            KeycloakAuthorz keycloakAuthorz = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), KeycloakAuthorz.class);
            if (keycloakAuthorz == null) {
                keycloakAuthorz = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), KeycloakAuthorz.class);
            }
            if (keycloakAuthorz != null) {
                String accessToken = request.getHeader("Authorization");
                if (accessToken == null) {
                    throw new RuntimeException("Access token is missing");
                }
                request.setAttribute("resource", keycloakAuthorz.resource());
                request.setAttribute("scope", keycloakAuthorz.scope().getScope());
            }
        }
        filterChain.doFilter(request, response);
        log.info("KeycloakAuthorzFilter is running...");
    }
}