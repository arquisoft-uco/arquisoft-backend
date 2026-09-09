package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EstadoFichaPerfilResponseDTO(
        String id,
        String nombre,
        Instant fechaActualizacion
) {
}
