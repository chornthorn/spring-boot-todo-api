package com.khodedev.app.common.types;

public enum Scope {
    READ, READBY, CREATE, UPDATE, DELETE;

    public static Scope fromString(String scope) {
        for (Scope s : Scope.values()) {
            if (s.name().equalsIgnoreCase(scope)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No constant with text " + scope + " found");
    }

    // getter
    public String getScope() {
        return this.name();
    }
}