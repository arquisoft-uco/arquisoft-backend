package com.arquisoft.seguridad.domain.model;

/**
 * Roles contextuales del sistema definidos en Keycloak (ADR-003).
 * El código de cada rol coincide exactamente con el nombre emitido
 * en el claim realm_access.roles del token JWT (SCREAMING_CASE).
 *
 * Estos roles representan las responsabilidades que puede ejercer un usuario
 * dentro del contexto de negocio de Arquisoft.
 */
public enum UsuarioRole {
    ESTUDIANTE("ESTUDIANTE", "Estudiante que presenta proyecto de grado"),
    ASESOR("ASESOR", "Asesor asignado a un proyecto de grado"),
    ASESOR_FICHA("ASESOR_FICHA", "Asesor que apoya la elaboracion de fichas de perfil"),
    COORDINADOR("COORDINADOR", "Coordinador del programa que gestiona proyectos"),
    JURADO("JURADO", "Jurado que evalua proyectos de grado"),
    BIBLIOTECARIO("BIBLIOTECARIO", "Bibliotecario que gestiona consulta de PG"),
    REPRESENTANTE_COMITE_CURRICULUM("REPRESENTANTE_COMITE_CURRICULUM", "Representante que aprueba fichas de perfil"),
    ADMINISTRADOR("ADMINISTRADOR", "Administrador del sistema");

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
     * Busca el rol a partir del codigo exacto emitido por Keycloak en realm_access.roles.
     */
    public static UsuarioRole fromCode(String code) {
        for (UsuarioRole role : UsuarioRole.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Rol desconocido: " + code);
    }

    /**
     * Retorna el nombre con prefijo ROLE_ que Spring Security utiliza.
     * Ejemplo: ESTUDIANTE -> ROLE_ESTUDIANTE
     */
    public String getSpringRole() {
        return "ROLE_" + this.name();
    }
}
