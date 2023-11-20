package com.khodedev.app.common.controllers;

import com.khodedev.app.common.exceptions.ForbiddenException;
import com.khodedev.app.common.exceptions.NotFoundException;
import com.khodedev.app.common.exceptions.UnauthorizedException;
import com.khodedev.app.common.types.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request, HttpServletResponse response) {
        var statusCodeAttribute = request.getAttribute("statusCode");

        if (statusCodeAttribute != null) {
            handleStatusCodeError((int) statusCodeAttribute);
        } else {
            handleResponseStatusError(response.getStatus(), request.getRequestURI());
        }

        return null; // Returning null as ResponseEntity will be handled by exception handlers
    }

    private void handleStatusCodeError(int statusCode) {
        switch (statusCode) {
            case 401:
                throw new UnauthorizedException("Unauthorized");
            case 403:
                throw new ForbiddenException("Forbidden");
            default:
                log.warning("Unexpected status code: " + statusCode);
        }
    }

    private void handleResponseStatusError(int status, String requestURI) {
        switch (status) {
            case 404:
                throw new NotFoundException("The requested route was not found");
            case 401:
                throw new UnauthorizedException("Unauthorized");
            case 403:
                throw new ForbiddenException("Forbidden");
            case 500:
                throw new RuntimeException("Internal server error");
            default:
                log.warning("Unexpected HTTP status: " + status + " for URI: " + requestURI);
        }
    }
}
