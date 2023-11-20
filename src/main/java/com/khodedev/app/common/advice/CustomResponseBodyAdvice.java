package com.khodedev.app.common.advice;

import com.khodedev.app.common.annotations.SkipResponseWrapper;
import com.khodedev.app.common.types.ErrorResponse;
import com.khodedev.app.common.types.ResponseWrapper;
import io.micrometer.common.lang.NonNull;
import lombok.extern.java.Log;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.core.annotation.AnnotatedElementUtils;

@Log
@ControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {
        // Get the class that contains the method
        returnType.getContainingClass();

        // Check if the method is not null
        if (returnType.getMethod() != null) {
            // Return true if the class or the method does not have the SkipResponseWrapper annotation
            return !AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), SkipResponseWrapper.class) &&
                    !AnnotatedElementUtils.hasAnnotation(returnType.getMethod(), SkipResponseWrapper.class);
        }

        // If the method is null, apply the advice by default
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {
        if (body instanceof ErrorResponse error) {
            return new ErrorResponse(error.getStatusCode(), error.getMessage(), error.getData());
        } else if (body instanceof String | body instanceof Number || body instanceof Boolean || body instanceof Character) {
            return body;
        } else {
            return new ResponseWrapper<>("success", 200, body);
        }
    }
}