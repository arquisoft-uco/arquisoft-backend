package com.arquisoft.shared.message.annotation;

public final class ApiSecurity {

    private ApiSecurity() {}

    // Nombre del esquema de seguridad OpenAPI. Es un identificador de contrato, no texto:
    // @SecurityScheme lo declara una vez y cada @SecurityRequirement lo referencia por este
    // nombre exacto. Si dejan de coincidir, springdoc no falla — simplemente omite el candado
    // en Swagger UI, asi que la unica proteccion es que exista una sola declaracion.
    public static final String BEARER_AUTH = "bearerAuth";
}
