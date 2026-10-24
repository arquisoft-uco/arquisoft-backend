package com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ObservacionResponseDTO(
        UUID id,
        UUID revisionAsesor,
        String descripcion,
        String estado,
        String estadoNombre
) {
}
