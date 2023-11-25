package com.khodedev.app.common.filters;

import com.khodedev.app.common.annotations.PreAuthz;
import com.khodedev.app.common.constants.Constants;
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
public class PreAuthzFilter extends OncePerRequestFilter {

    private final RequestMappingHandlerMapping handlerMapping;

    public PreAuthzFilter(RequestMappingHandlerMapping handlerMapping) {
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
            var isPublic = request.getAttribute(Constants.IS_PUBLIC);
            if (isPublic != null && (boolean) isPublic) {
                filterChain.doFilter(request, response);
                return;
            }

            // Attempt to get the handler execution chain for the request
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);

            if (handlerExecutionChain != null) {
                // Extract handler method and check for the PreAuthz annotation
                HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
                var preAuthz = findPreAuthzAnnotation(handlerMethod);
                checkPreAuthz(request, response, preAuthz);
            }

            // Continue with the filter chain
            filterChain.doFilter(request, response);
            log.info("PreAuthzFilter is running...");

        } catch (Exception e) {
            // Handle exceptions that may occur while processing the request
            throw new RuntimeException(e);
        }
    }

    private void checkPreAuthz(HttpServletRequest request, HttpServletResponse response, PreAuthz preAuthz) {
        if (preAuthz != null) {
            // Check if Authorization header is present
            String accessToken = request.getHeader(Constants.AUTHORIZATION);
            if (accessToken == null) {
                // Set response status to 401 (Unauthorized) if Authorization header is missing
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                // Set resource and scope attributes based on PreAuthz annotation
                request.setAttribute(Constants.RESOURCE, preAuthz.resource());
                request.setAttribute(Constants.SCOPE, preAuthz.scope().getScope());
            }
        }
    }

    private PreAuthz findPreAuthzAnnotation(HandlerMethod handlerMethod) {
        // Find PreAuthz annotation on the method or the class level
        var preAuthz = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), PreAuthz.class);
        if (preAuthz == null) {
            preAuthz = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), PreAuthz.class);
        }
        return preAuthz;
    }
}
