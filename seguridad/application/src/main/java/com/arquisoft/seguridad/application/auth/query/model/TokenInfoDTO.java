package com.arquisoft.seguridad.application.auth.query.model;

import java.util.List;

/**
 * Resultado tipado de la extraccion de informacion de un token JWT.
 * Exclusivo del lado query — nunca importado desde paquetes command.
 */
public record TokenInfoDTO(
        String keycloakUserId,
        String email,
        String name,
        List<String> roles,
        long issuedAt,
        long expiresAt
) {}
