package dev.oth.gbs.config;

import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.filter.RequiredRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableAspectJAutoProxy
public class UrlRoleMappingConfig {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    public UrlRoleMappingConfig(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Bean
    public Map<String, Map<String, String[]>> urlRoleMap() {
        System.out.println("urlRoleMap return");
        Map<String, Map<String, String[]>> urlRoleMap = new HashMap<>();

        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            RequiredRole requiredRole = handlerMethod.getMethodAnnotation(RequiredRole.class);
            if (requiredRole == null) {
                requiredRole = handlerMethod.getBeanType().getAnnotation(RequiredRole.class);
            }

            if (requiredRole != null) {
                String[] roles = Arrays.stream(requiredRole.value())
                        .map(UserRole::name)
                        .toArray(String[]::new);

                // HTTP 메서드와 URL 경로를 키로 하는 맵 설정
                requestMappingInfo.getMethodsCondition().getMethods().forEach(httpMethod -> {
                    if (requestMappingInfo.getPatternsCondition() != null && !requestMappingInfo.getPatternsCondition().getPatterns().isEmpty()) {
                        requestMappingInfo.getPatternsCondition().getPatterns()
                                .forEach(url -> urlRoleMap
                                        .computeIfAbsent(url, k -> new HashMap<>())
                                        .put(httpMethod.name(), roles));
                    }
                    if (requestMappingInfo.getPathPatternsCondition() != null) {
                        requestMappingInfo.getPathPatternsCondition().getPatterns()
                                .forEach(url -> urlRoleMap
                                        .computeIfAbsent(url.toString(), k -> new HashMap<>())
                                        .put(httpMethod.name(), roles));
                    }
                });
            }
        });

        System.out.println("urlRoleMap return " + urlRoleMap);
        return urlRoleMap;
    }

}