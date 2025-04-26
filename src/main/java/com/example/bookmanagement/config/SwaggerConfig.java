package com.example.bookmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("https://book-management-api-production-6e77.up.railway.app");
        server.setDescription("Production Server");

        return new OpenAPI()
                .info(new Info()
                        .title("Book Management API")
                        .version("1.0")
                        .description("API for managing books"))
                .servers(List.of(server));
    }
}