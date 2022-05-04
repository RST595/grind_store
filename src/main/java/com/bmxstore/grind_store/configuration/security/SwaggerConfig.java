package com.bmxstore.grind_store.configuration.security;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
//use this annotation to enable security check in swager - @SecurityScheme(name = "swagger", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

    @Bean
    public OpenAPI grindStoreOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Grind_Store shop API")
                        .description("Grind_Store shop sample application")
                        .version("v1.0.0")
                        .contact(new Contact()
                        .name("Max")
                        .url("http://localhost:8080/")
                        .email("testmail@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }


}


