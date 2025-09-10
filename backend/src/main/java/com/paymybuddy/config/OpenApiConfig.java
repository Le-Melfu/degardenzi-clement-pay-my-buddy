package com.paymybuddy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Pay My Buddy API")
                                                .description("API pour l'application Pay My Buddy - Projet OpenClassrooms")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Pay My Buddy Team")
                                                                .email("contact@paymybuddy.com"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Serveur de développement")))
                                .components(new Components()
                                                .addSecuritySchemes("sessionAuth", new SecurityScheme()
                                                                .type(SecurityScheme.Type.APIKEY)
                                                                .in(SecurityScheme.In.COOKIE)
                                                                .name("JSESSIONID")
                                                                .description("Authentification par session - Cookie JSESSIONID requis")));
        }
}
