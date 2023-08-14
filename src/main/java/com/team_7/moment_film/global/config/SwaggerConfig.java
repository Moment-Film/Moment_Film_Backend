package com.team_7.moment_film.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Moment Film 프로젝트 API Document")
                .version("v0.0.1")
                .description("Moment Film 프로젝트의 API 명세서입니다.");

        // Security 스키마 설정
        SecurityScheme apiKeyAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name("accessToken")
                .in(SecurityScheme.In.HEADER);

        // Security 요청 설정
        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("accessToken");

        return new OpenAPI()
                // Security 인증 컴포넌트 설정
                .components(new Components().addSecuritySchemes("accessToken", apiKeyAuth))
                // API 마다 Security 인증 컴포넌트 설정
                .addSecurityItem(addSecurityItem)
                .info(info);
    }
}