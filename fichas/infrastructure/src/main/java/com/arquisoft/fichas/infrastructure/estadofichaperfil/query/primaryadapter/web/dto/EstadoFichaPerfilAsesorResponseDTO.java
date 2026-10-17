package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EstadoFichaPerfilAsesorResponseDTO(
        UUID fichaPerfil,
        String tituloProyecto,
        String estadoId,
        String estadoNombre,
        Instant fechaActualizacion
) {
}
