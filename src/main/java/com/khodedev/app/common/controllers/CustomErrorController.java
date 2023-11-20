package com.khodedev.app.common.controllers;

import com.khodedev.app.common.types.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log
@RestController
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request){
        var errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "The requested route was not found",
               "",
                List.of()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}