package com.arquisoft.seguridad.infrastructure.config.keycloak;

import com.arquisoft.seguridad.domain.usuario.model.UsuarioRole;

import java.util.List;
import java.util.Optional;

/**
 * Regla de negocio pura: traduce códigos de rol (emitidos por Keycloak)
 * en el nombre de autoridad que Spring Security reconoce.
 * Sin prefijo ROLE_ — compatible con hasAuthority() en @PreAuthorize.
 * Sin dependencias de framework — Java 21 puro.
 */
public final class RoleAuthorityMapper {

    private RoleAuthorityMapper() {}

    /**
     * Convierte un código de rol a su authority name (coincide con el código kebab-case).
     * Retorna vacío si el código no pertenece al dominio.
     */
    public static Optional<String> toAuthorityName(String roleCode) {
        try {
            UsuarioRole role = UsuarioRole.fromCode(roleCode);
            return Optional.of(role.getCode());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Filtra y mapea una lista de códigos de rol a sus nombres de autoridad.
     * Los códigos internos de Keycloak (offline_access, etc.) son ignorados.
     */
    public static List<String> toAuthorityNames(List<String> roleCodes) {
        return roleCodes.stream()
                .map(RoleAuthorityMapper::toAuthorityName)
                .flatMap(Optional::stream)
                .toList();
    }
}
