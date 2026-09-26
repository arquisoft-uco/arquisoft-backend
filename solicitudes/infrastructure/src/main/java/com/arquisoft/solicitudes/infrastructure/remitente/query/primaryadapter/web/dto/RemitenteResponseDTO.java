package com.arquisoft.solicitudes.infrastructure.remitente.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RemitenteResponseDTO(
        UUID usuarioId,
        String identificador,
        String nombre,
        String email
) {
}
