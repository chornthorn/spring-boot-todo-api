package com.khodedev.app.controllers;

import com.khodedev.app.common.annotations.KeycloakAuthorz;
import com.khodedev.app.common.annotations.Public;
import com.khodedev.app.common.types.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @Public
    @GetMapping("/demo")
    public String demo() {
        return "This is a demo!";
    }

    @GetMapping("/api")
    @KeycloakAuthorz(resource = "api", scope = Scope.READ)
    public String api() {
        return "This is an api!";
    }

    @Public
    @GetMapping("/category")
    @KeycloakAuthorz(scope = Scope.UPDATE, resource = "category")
    public String getCategory() {
        return "Category data";
    }
}
