package com.membership.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI membershipServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Membership Service API")
                        .description("Subscription-based membership platform with configurable tiers and benefits")
                        .version("1.0.0"));
    }
}
