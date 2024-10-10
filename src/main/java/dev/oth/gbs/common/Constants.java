package dev.oth.gbs.common;

public class Constants {
    public static final String VERSION = "1.0.0";
    public static final String[] authIgnorePaths = {
            "/swagger-ui/**", "/v3/api-docs/**",
            "/api/auth/**",
    };
    public static final String[] adminIgnorePaths = {
            "/api/auth/admin/users",
    };

}
