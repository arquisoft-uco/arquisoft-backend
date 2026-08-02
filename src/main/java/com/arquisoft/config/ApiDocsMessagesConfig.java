package com.arquisoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Publica los textos de documentación OpenAPI en el {@code Environment}.
 *
 * <p>springdoc resuelve los {@code ${clave}} de {@code FichasApiKeys} en las anotaciones
 * {@code @Tag} / {@code @Operation} / {@code @ApiResponse} contra el {@code Environment} de Spring,
 * no contra el {@code ResourceBundle}. Cargar aquí el mismo archivo que lee el catálogo mantiene
 * una única fuente de verdad para esos textos.
 *
 * <p>El {@code encoding = "UTF-8"} es obligatorio: sin él Spring lee los {@code .properties} en
 * ISO-8859-1 y las tildes llegan corruptas a Swagger.
 */
@Configuration
@PropertySource(value = "classpath:messages/fichas-api.properties", encoding = "UTF-8")
public class ApiDocsMessagesConfig {
}
