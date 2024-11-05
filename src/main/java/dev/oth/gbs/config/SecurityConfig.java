package dev.oth.gbs.config;

import dev.oth.gbs.common.Constants;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Map;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final Map<String, Map<String, String[]>> urlRoleMap;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, Map<String, Map<String, String[]>> urlRoleMap) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.urlRoleMap = urlRoleMap;
        System.out.println("SecurityConfig initialize");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    urlRoleMap.forEach((url, methodsMap) -> {
                        methodsMap.forEach((method, roles) -> {
                            System.out.println("url: " + url + ", method: " + method + ", roles: " + Arrays.toString(roles));

                            if (Arrays.asList(roles).contains(UserRole.ROLE_PUBLIC.name())) {
                                auth.requestMatchers(HttpMethod.valueOf(method), url).permitAll(); // 공개 URL은 인증 없이 허용
                                return;
                            }
                            if (Arrays.asList(roles).contains(UserRole.ROLE_ANY.name())) {
                                auth.requestMatchers(HttpMethod.valueOf(method), url).authenticated(); // ROLE_ANY는 로그인된 사용자만 접근 가능
                                return;
                            }
                            if (Arrays.asList(roles).contains(UserRole.ROLE_SELF.name())) {
                                auth.requestMatchers(HttpMethod.valueOf(method), url).authenticated(); // ROLE_SELF는 자기 자신, 처리는 RoleCheckASpect File에서 처리.
                                return;
                            }
                            auth.requestMatchers(HttpMethod.valueOf(method), url).hasAnyAuthority(roles); // 특정 권한이 필요한 URL
                        });
                    });

                    auth.requestMatchers(Constants.swaggerPaths).permitAll();
                    auth.anyRequest().authenticated(); // 그 외 모든 요청은 인증 필요
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
