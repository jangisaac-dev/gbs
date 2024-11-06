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
import java.util.Arrays;
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
    private Map<String, Map<String, String[]>> urlRoleMap; // URL과 HTTP 메서드로 권한을 확인

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String httpMethod = request.getMethod();

        System.out.println("JwtRequestFilter / " + requestUri + " [Method: " + httpMethod + "]");

        // URL이 /public 경로로 시작하면 토큰 검증 없이 통과
        if (urlRoleMap.containsKey(requestUri) && urlRoleMap.get(requestUri).containsKey(httpMethod)) {
            String[] roles = urlRoleMap.get(requestUri).get(httpMethod);

            if (Arrays.asList(roles).contains(UserRole.ROLE_PUBLIC.toString())) {
                chain.doFilter(request, response);
                return;
            }
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
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
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // validateToken 확인용 로그 추가
            if (jwtTokenUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 설정되는 정보 로그 출력
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("getAuthorities : " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
        }

        chain.doFilter(request, response);
    }
}

