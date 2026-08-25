package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.keycloak.mapper;

import com.arquisoft.seguridad.application.auth.command.secondaryport.model.CredencialesProveedor;

import java.util.Map;

public final class KeycloakCredencialesMapper {

    // Claves de la respuesta del token endpoint de OAuth2/OIDC: las fija el estandar y
    // Keycloak las emite con estos nombres exactos, son contrato y no texto de catalogo.
    private static final String RESPUESTA_ACCESS_TOKEN = "access_token";
    private static final String RESPUESTA_REFRESH_TOKEN = "refresh_token";
    private static final String RESPUESTA_EXPIRES_IN = "expires_in";
    private static final String RESPUESTA_TOKEN_TYPE = "token_type";
    private static final String RESPUESTA_SCOPE = "scope";

    private static final long EXPIRACION_POR_DEFECTO = 3600L;
    private static final String TOKEN_TYPE_POR_DEFECTO = "Bearer";
    private static final String SCOPE_POR_DEFECTO = "";

    private KeycloakCredencialesMapper() {}

    public static CredencialesProveedor toModel(Map<String, Object> respuesta) {
        return new CredencialesProveedor(
                (String) respuesta.get(RESPUESTA_ACCESS_TOKEN),
                (String) respuesta.get(RESPUESTA_REFRESH_TOKEN),
                ((Number) respuesta.getOrDefault(RESPUESTA_EXPIRES_IN, EXPIRACION_POR_DEFECTO)).longValue(),
                (String) respuesta.getOrDefault(RESPUESTA_TOKEN_TYPE, TOKEN_TYPE_POR_DEFECTO),
                (String) respuesta.getOrDefault(RESPUESTA_SCOPE, SCOPE_POR_DEFECTO));
    }
}
