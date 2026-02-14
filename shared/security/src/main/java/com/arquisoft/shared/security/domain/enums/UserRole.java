package com.arquisoft.shared.security.domain.enums;

/**
 * Enumeración de roles disponibles en el sistema.
 * Estos roles se asignan en Keycloak y se obtienen a través del token JWT.
 */
public enum UserRole {
    ASESOR_FICHA("asesor_ficha", "Asesor de Ficha"),
    JURADO("jurado", "Jurado"),
    BIBLIOTECARIO("bibliotecario", "Bibliotecario"),
    ADMINISTRADOR("administrador", "Administrador"),
    ESTUDIANTE("estudiante", "Estudiante"),
    ASESOR("asesor", "Asesor"),
    COORDINADOR("coordinador", "Coordinador"),
    REPRESENTANTE_COMITE_CURRICULUM("representante_comite_curriculum", "Representante Comité de Curriculum");

    private final String code;
    private final String description;

    UserRole(String code, String description) {
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
     * Obtiene el rol a partir del código.
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Rol desconocido: " + code);
    }

    /**
     * Obtiene el nombre con prefijo ROLE_ que Spring Security utiliza.
     */
    public String getSpringRole() {
        return "ROLE_" + this.name();
    }
}
