package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.keycloak.mapper;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduce el {@link RegistroIdentidadEntity} al {@code UserRepresentation} y al
 * {@code RoleRepresentation} del Admin REST API de Keycloak, ambos como {@code Map} para no
 * arrastrar los tipos de {@code keycloak-admin-client} al classpath (política del proyecto).
 */
public final class KeycloakUsuarioRepresentationMapper {

    // Nombres de campo del Admin REST API de Keycloak. No son catálogo: los fija Keycloak y los
    // compara literalmente, igual que los parámetros OAuth de KeycloakAuthOutputAdapter.
    private static final String CAMPO_ID = "id";
    private static final String CAMPO_USERNAME = "username";
    private static final String CAMPO_EMAIL = "email";
    private static final String CAMPO_FIRST_NAME = "firstName";
    private static final String CAMPO_LAST_NAME = "lastName";
    private static final String CAMPO_ENABLED = "enabled";
    private static final String CAMPO_EMAIL_VERIFIED = "emailVerified";
    private static final String CAMPO_NAME = "name";

    private KeycloakUsuarioRepresentationMapper() {}

    public static Map<String, Object> toUserRepresentation(RegistroIdentidadEntity registro) {
        var representacion = new LinkedHashMap<String, Object>();
        representacion.put(CAMPO_USERNAME, registro.email());
        representacion.put(CAMPO_EMAIL, registro.email());
        representacion.put(CAMPO_FIRST_NAME, registro.nombres());
        representacion.put(CAMPO_LAST_NAME, registro.apellidos());
        representacion.put(CAMPO_ENABLED, true);
        representacion.put(CAMPO_EMAIL_VERIFIED, true);
        return representacion;
    }

    public static Map<String, Object> toRoleRepresentation(String id, String name) {
        var rol = new LinkedHashMap<String, Object>();
        rol.put(CAMPO_ID, id);
        rol.put(CAMPO_NAME, name);
        return rol;
    }
}
