package dev.oth.gbs.config;

import dev.oth.gbs.common.Constants;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.filter.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final Map<String, String> urlRoleMap;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, Map<String, String> urlRoleMap) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.urlRoleMap = urlRoleMap;
        System.out.println("SecurityConfig initialize");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    urlRoleMap.forEach((url, role) -> {
                        System.out.println("url: " + url + ", role: " + role);
                        if (UserRole.ROLE_PUBLIC.name().equals(role)) {
                            auth.requestMatchers(url).permitAll(); // 공개 URL은 인증 없이 허용
                            return;
                        }
                        if (UserRole.ROLE_ANY.name().equals(role)) {
                            auth.requestMatchers(url).authenticated(); // ROLE_ANY는 로그인된 사용자만 접근 가능
                            return;
                        }
                        auth.requestMatchers(url).hasAuthority(role); // 특정 권한이 필요한 URL
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


//    @Bean
//    public UserDetailsService userDetailsService() {
//        // 사용자 정보를 메모리에 저장하는 간단한 예시
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("admin")
//                .password(passwordEncoder().encode("password"))
//                .roles("ADMIN", "USER")  // ADMIN 권한 추가
//                .build());
//        return manager;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
