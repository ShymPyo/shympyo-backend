package shympyo.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
@OpenAPIDefinition(
        servers = { @Server(url = "http://localhost:8080", description = "Local") }
)
public class OpenApiConfigLocal {
    private static final String SECURITY_SCHEME_NAME = "BearerAuth";
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Shympyo API").version("v1").description("Shympyo 백엔드 API 문서"))
                .components(new Components().addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme().name(SECURITY_SCHEME_NAME).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
