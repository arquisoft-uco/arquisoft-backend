package com.arquisoft.solicitudes.infrastructure.destinatario.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DestinatarioResponseDTO(
        UUID usuarioId,
        String identificador,
        String nombre,
        String email
) {
}
