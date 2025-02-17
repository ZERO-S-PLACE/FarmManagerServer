package org.zeros.farm_manager_server.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.*;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Farm Manager API", version = "1.0"),
        security = @SecurityRequirement(name = "oauth2Auth"))
public class OpenApiConfig {
        @Bean
        @Primary
        public OpenApiCustomizer openAPICustomizer() {
                return openAPI -> {
                        List<io.swagger.v3.oas.models.tags.Tag> tags = openAPI.getTags();
                        if (tags != null) {
                                // Sort the tags by their name or other custom criteria
                                tags.sort(Comparator.comparing(Tag::getName));
                        }
                };
        }

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI();
        }
}
