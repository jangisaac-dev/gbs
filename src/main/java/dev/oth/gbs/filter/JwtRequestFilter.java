package dev.oth.gbs.filter;

import com.google.gson.JsonObject;
import dev.oth.gbs.domain.TokenDetailModel;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.interfaces.CustomUserDetailsService;
import dev.oth.gbs.providers.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Lazy
    private CustomUserDetailsService userDetailsService;

    @Autowired
    @Lazy
    private Map<String, String> urlRoleMap;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        System.out.println("JwtRequestFilter / " + request.getRequestURI());
        // URL이 /public 경로로 시작하면 토큰 검증 없이 통과
        if (urlRoleMap.get(request.getRequestURI()).equals(UserRole.ROLE_PUBLIC.toString())) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            System.out.println("log 1");
            jwt = authorizationHeader.substring(7);
            try {
                TokenDetailModel tokenData = jwtTokenUtil.extractValue(jwt);
                email = tokenData.getEmail();
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            } catch (Exception e) {
                System.out.println("Error parsing JWT Token: " + e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("log 2");
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // validateToken 확인용 로그 추가
            if (jwtTokenUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 설정되는 정보 로그 출력
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (urlRoleMap.get(request.getRequestURI()).equals(UserRole.ROLE_ANY.toString())) {
                    System.out.println("This API for any user");
                    chain.doFilter(request, response);
                    return;
                }
                else {
                    System.out.println("request.getRequestURI() : " + request.getRequestURI() + "/ urlRoleMap : " + urlRoleMap);
                }

                System.out.println("role : " + userDetails.getAuthorities());
                System.out.println("getAuthorities : " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            } else {
                System.out.println("JWT validation failed for user: " + userDetails.getUsername());
            }
        }
        System.out.println("log 3");


        chain.doFilter(request, response);
    }
}
