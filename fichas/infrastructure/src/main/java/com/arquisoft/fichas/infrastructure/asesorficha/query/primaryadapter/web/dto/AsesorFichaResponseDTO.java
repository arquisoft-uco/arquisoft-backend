package com.arquisoft.fichas.infrastructure.asesorficha.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AsesorFichaResponseDTO(
        UUID id,
        String identificador,
        String nombre,
        String email
) {
}
