package com.arquisoft.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Arquisoft API",
        version = "1.0.0",
        description = "API REST del sistema de gestion de proyectos de grado — Universidad Cooperativa de Colombia. "
            + "Autenticacion via Keycloak (OAuth2/OIDC). "
            + "Contextos: Seguridad, Usuarios, Fichas de Perfil, Proyectos de Grado, "
            + "Artefactos, Repositorio de Artefactos, Entregables y Evaluaciones.",
        contact = @Contact(
            name = "Equipo Arquisoft UCO",
            email = "arquisoft@uco.edu.co"
        )
/*            ,
        license = @License(
            name = "Uso interno UCO",
            url = "https://www.uco.edu.co"
        )*/
    ),
    servers = {
        @Server(url = "/api", description = "Servidor local de desarrollo")
/*            ,
        @Server(url = "https://arquisoft.uco.edu.co/api", description = "Servidor de produccion UCO")*/
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER,
    description = "Token JWT obtenido desde Keycloak via POST /api/auth/login. "
        + "Formato: Authorization: Bearer <token>"
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
            .group("00-todos")
            .displayName("Todos los endpoints")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public GroupedOpenApi seguridadApi() {
        return GroupedOpenApi.builder()
            .group("01-seguridad")
            .displayName("Seguridad")
            .pathsToMatch("/auth/**", "/seguridad/**")
            .build();
    }

    @Bean
    public GroupedOpenApi usuariosApi() {
        return GroupedOpenApi.builder()
            .group("02-usuarios")
            .displayName("Usuarios")
            .pathsToMatch("/usuarios/**")
            .build();
    }

    @Bean
    public GroupedOpenApi fichasApi() {
        return GroupedOpenApi.builder()
            .group("03-fichas")
            .displayName("Fichas de Perfil")
            .pathsToMatch("/fichas-perfil/**")
            .build();
    }

    @Bean
    public GroupedOpenApi proyectosApi() {
        return GroupedOpenApi.builder()
            .group("04-proyectos")
            .displayName("Proyectos de Grado")
            .pathsToMatch("/proyectos/**")
            .build();
    }

    @Bean
    public GroupedOpenApi artefactosApi() {
        return GroupedOpenApi.builder()
            .group("05-artefactos")
            .displayName("Artefactos")
            .pathsToMatch("/artefactos/**")
            .build();
    }

    @Bean
    public GroupedOpenApi repositorioArtefactosApi() {
        return GroupedOpenApi.builder()
            .group("06-repositorio-artefactos")
            .displayName("Repositorio de Artefactos")
            .pathsToMatch("/repositorio-artefactos/**")
            .build();
    }

    @Bean
    public GroupedOpenApi entregablesApi() {
        return GroupedOpenApi.builder()
            .group("07-entregables")
            .displayName("Entregables")
            .pathsToMatch("/entregables/**")
            .build();
    }

    @Bean
    public GroupedOpenApi evaluacionesApi() {
        return GroupedOpenApi.builder()
            .group("08-evaluaciones")
            .displayName("Evaluaciones")
            .pathsToMatch("/evaluaciones/**")
            .build();
    }
}
