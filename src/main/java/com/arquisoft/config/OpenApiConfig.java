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

/**
 * Configuracion global de OpenAPI / Swagger UI para Arquisoft.
 *
 * <p>Reside en el paquete config de la aplicacion principal porque es la unica
 * capa que ensambla todos los contextos y tiene visibilidad completa de la API.
 * No duplicar en modulos individuales.
 *
 * <p>Define:
 * - Metadata global de la API (titulo, version, contacto, licencia, servidores)
 * - Esquema de seguridad JWT bearerAuth aplicado por defecto a todos los endpoints
 * - Grupos de API por bounded context para navegacion organizada en Swagger UI
 *
 * <p>Acceso en desarrollo: http://localhost:8080/api/swagger-ui/index.html
 * (sin autenticacion — endpoints /swagger-ui/** y /v3/api-docs/** son permit-all)
 *
 * <p>ADR-011: documentacion-api-springdoc-openapi
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Arquisoft API",
        version = "1.0.0",
        description = "API REST del sistema de gestion de proyectos de grado — Universidad Cooperativa de Colombia. "
            + "Autenticacion via Keycloak (OAuth2/OIDC). "
            + "Contextos: Seguridad, Fichas de Perfil, Proyectos de Grado, "
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

    /**
     * Grupo: todos los endpoints de la API (vista completa).
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
            .group("00-todos")
            .displayName("Todos los endpoints")
            .pathsToMatch("/**")
            .build();
    }

    /**
     * Grupo: Seguridad — autenticacion, tokens, validacion y gestion de usuarios.
     * Incluye: /auth/** (login, refresh, logout, validate) y /seguridad/** (usuarios)
     */
    @Bean
    public GroupedOpenApi seguridadApi() {
        return GroupedOpenApi.builder()
            .group("01-seguridad")
            .displayName("Seguridad")
            .pathsToMatch("/auth/**", "/seguridad/**")
            .build();
    }

    /**
     * Grupo: Fichas de Perfil — ciclo de vida de fichas de trabajos de grado.
     */
    @Bean
    public GroupedOpenApi fichasApi() {
        return GroupedOpenApi.builder()
            .group("02-fichas")
            .displayName("Fichas de Perfil")
            .pathsToMatch("/fichas-perfil/**")
            .build();
    }

    /**
     * Grupo: Proyectos de Grado — gestion de proyectos, asesores y estudiantes.
     */
    @Bean
    public GroupedOpenApi proyectosApi() {
        return GroupedOpenApi.builder()
            .group("03-proyectos")
            .displayName("Proyectos de Grado")
            .pathsToMatch("/proyectos/**")
            .build();
    }

    /**
     * Grupo: Artefactos — documentos y artefactos versionados.
     */
    @Bean
    public GroupedOpenApi artefactosApi() {
        return GroupedOpenApi.builder()
            .group("04-artefactos")
            .displayName("Artefactos")
            .pathsToMatch("/artefactos/**")
            .build();
    }

    /**
     * Grupo: Repositorio de Artefactos — control de versiones y almacenamiento.
     */
    @Bean
    public GroupedOpenApi repositorioArtefactosApi() {
        return GroupedOpenApi.builder()
            .group("05-repositorio-artefactos")
            .displayName("Repositorio de Artefactos")
            .pathsToMatch("/repositorio-artefactos/**")
            .build();
    }

    /**
     * Grupo: Entregables — entregables e hitos de proyectos de grado.
     */
    @Bean
    public GroupedOpenApi entregablesApi() {
        return GroupedOpenApi.builder()
            .group("06-entregables")
            .displayName("Entregables")
            .pathsToMatch("/entregables/**")
            .build();
    }

    /**
     * Grupo: Evaluaciones — evaluaciones definitivas y calificaciones.
     */
    @Bean
    public GroupedOpenApi evaluacionesApi() {
        return GroupedOpenApi.builder()
            .group("07-evaluaciones")
            .displayName("Evaluaciones")
            .pathsToMatch("/evaluaciones/**")
            .build();
    }
}
