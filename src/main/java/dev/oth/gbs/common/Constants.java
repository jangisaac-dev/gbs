package dev.oth.gbs.common;

public class Constants {
    public static final String[] authIgnorePaths = {
            "/swagger-ui/**", "/v3/api-docs/**",
            "/api/auth/**",
    };
    public static final String[] adminIgnorePaths = {
            "/api/admin/**",
    };
}
