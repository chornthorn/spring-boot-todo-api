package com.khodedev.app.common.filters;

import com.khodedev.app.common.annotations.Public;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
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
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        HandlerExecutionChain handlerExecutionChain = null;
        try {
            handlerExecutionChain = handlerMapping.getHandler(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (handlerExecutionChain == null) {
            filterChain.doFilter(request, response);
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
        Public publicAccess = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Public.class);
        if (publicAccess != null) {
            log.info("PublicAccessFilter: " + request.getRequestURI() + " is public");
            // set isPublic to true to header
            request.setAttribute("isPublic", true);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("PublicAccessFilter: " + request.getRequestURI() + " is not public");
        filterChain.doFilter(request, response);
    }
}