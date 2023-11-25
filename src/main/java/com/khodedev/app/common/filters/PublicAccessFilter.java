package com.khodedev.app.common.filters;

import com.khodedev.app.common.annotations.Public;
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
public class PublicAccessFilter extends OncePerRequestFilter {

    private final RequestMappingHandlerMapping handlerMapping;

    public PublicAccessFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Attempt to get the handler execution chain for the request
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);

            if (handlerExecutionChain == null) {
                // No handler found, continue with the filter chain
                filterChain.doFilter(request, response);
                return;
            }

            // Extract handler method and check for the Public annotation
            HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
            Public publicAccess = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Public.class);

            if (publicAccess != null) {
                // The endpoint is marked as public, allowing unrestricted access
                log.info("PublicAccessFilter: " + request.getRequestURI() + " is public");
                request.setAttribute(Constants.IS_PUBLIC, true);
                filterChain.doFilter(request, response);
                return;
            }

            // The endpoint is not marked as public, continue with the filter chain
            log.info("PublicAccessFilter: " + request.getRequestURI() + " is not public");
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Handle exceptions that may occur while processing the request
            throw new RuntimeException(e);
        }
    }
}
