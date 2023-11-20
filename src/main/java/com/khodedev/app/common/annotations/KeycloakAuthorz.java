package com.khodedev.app.common.annotations;

import com.khodedev.app.common.types.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeycloakAuthorz {
    String resource();
    Scope scope();
}

