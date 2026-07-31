package com.arquisoft.shared.web.openapi;

/**
 * Códigos de respuesta HTTP como constantes de compilación para la documentación
 * OpenAPI ({@code @ApiResponse(responseCode = ...)}).
 *
 * <p>Fuente única de los literales de estado — los adaptadores web no deben
 * quemar códigos de respuesta inline.</p>
 */
public final class ApiCodes {

    private ApiCodes() {}

    public static final String OK           = "200";
    public static final String CREATED      = "201";
    public static final String NO_CONTENT   = "204";
    public static final String BAD_REQUEST  = "400";
    public static final String UNAUTHORIZED = "401";
    public static final String FORBIDDEN    = "403";
    public static final String NOT_FOUND    = "404";
    public static final String CONFLICT     = "409";
    public static final String UNPROCESSABLE = "422";
}
