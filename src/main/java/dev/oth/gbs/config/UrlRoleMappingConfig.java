package dev.oth.gbs.config;

import dev.oth.gbs.filter.RequiredRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class UrlRoleMappingConfig {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    public UrlRoleMappingConfig(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Bean
    public Map<String, String> urlRoleMap() {
        System.out.println("urlRoleMap return");
        Map<String, String> urlRoleMap = new HashMap<>();

        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
//            System.out.println("foreach [requestMappingInfo :" + requestMappingInfo + "/handlerMethod : " + handlerMethod.getMethod().getName() + "]");
            RequiredRole requiredRole = handlerMethod.getMethodAnnotation(RequiredRole.class);
            if (requiredRole == null) {
                requiredRole = handlerMethod.getBeanType().getAnnotation(RequiredRole.class);
                System.out.println("requiredRole is Null");
            }
            if (requiredRole != null) {
                String role = requiredRole.value().length > 0 ? requiredRole.value()[0].name() : "ROLE_PUBLIC";

                // PatternsRequestCondition Null 체크
                if (requestMappingInfo.getPatternsCondition() != null && !requestMappingInfo.getPatternsCondition().getPatterns().isEmpty()) {
                    requestMappingInfo.getPatternsCondition().getPatterns()
                            .forEach(url -> urlRoleMap.put(url, role));
                } else if (requestMappingInfo.getPathPatternsCondition() != null) {
                    requestMappingInfo.getPathPatternsCondition().getPatterns()
                            .forEach(url -> urlRoleMap.put(url.toString(), role));
                } else {
                    System.out.println("No patterns found for: " + handlerMethod.getMethod().getName());
                }
            }
        });
        System.out.println("urlRoleMap return " + urlRoleMap);

        return urlRoleMap;
    }
}