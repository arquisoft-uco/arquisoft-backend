package com.arquisoft.usuarios.domain.usuario.model;

import com.arquisoft.shared.exception.DomainException;

/**
 * Roles de negocio del usuario, definidos en Keycloak (ADR-003).
 *
 * <p>El código de cada rol coincide exactamente con el nombre emitido en el claim
 * {@code realm_access.roles} del token JWT (kebab-case).
 */
public enum UsuarioRole {
    ESTUDIANTE("estudiante", "Estudiante que presenta proyecto de grado"),
    ASESOR("asesor", "Asesor asignado a un proyecto de grado"),
    ASESOR_FICHA("asesor-ficha", "Asesor que apoya la elaboracion de fichas de perfil"),
    COORDINADOR("coordinador", "Coordinador del programa que gestiona proyectos"),
    JURADO("jurado", "Jurado que evalua proyectos de grado"),
    BIBLIOTECARIO("bibliotecario", "Bibliotecario que gestiona consulta de PG"),
    REPRESENTANTE_COMITE_CURRICULUM("representante-comite", "Representante que aprueba fichas de perfil"),
    ADMINISTRADOR("administrador", "Administrador del sistema");

    private final String code;
    private final String description;

    UsuarioRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Busca el rol a partir del codigo emitido por Keycloak en realm_access.roles (case-insensitive).
     */
    public static UsuarioRole fromCode(String code) {
        for (UsuarioRole role : UsuarioRole.values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new DomainException("Rol desconocido: " + code, "ROL_DESCONOCIDO");
    }
}
