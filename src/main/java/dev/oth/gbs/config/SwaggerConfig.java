package dev.oth.gbs.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

import javax.annotation.PostConstruct;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Configuration
public class SwaggerConfig {

    @Autowired
    private Environment environment;

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;


    // Bearer 토큰을 사용하도록 SecurityScheme 구성
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")
                .in(SecurityScheme.In.HEADER);  // Authorization 헤더에 토큰을 포함
    }

    private OpenApiCustomizer createOpenApiCustomizer(String title, String version) {
        return openApi -> {
            openApi.info(new Info().title(title).version(version));
            openApi.schemaRequirement("bearerAuth", createAPIKeyScheme());
            openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        };
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .displayName("All API")
                .addOpenApiCustomizer(createOpenApiCustomizer("All API", "v1"))
                .build();
    }

    // 애플리케이션이 완전히 시작된 후 Swagger UI URL 출력
    @EventListener(ApplicationReadyEvent.class)
    public void printSwaggerUrl() {
        // 서버 호스트 및 포트 정보 가져오기
        String host = environment.getProperty("server.address", "localhost");
        int port = webServerAppCtxt.getWebServer().getPort();
        String contextPath = environment.getProperty("server.servlet.context-path", "");

        // Swagger UI URL 생성
        String swaggerUrl = String.format("http://%s:%d%s/swagger-ui/index.html", host, port, contextPath);
        System.out.println("Swagger UI available at: " + swaggerUrl);
    }
}
