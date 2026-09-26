package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ObservacionItemResponseDTO(
        UUID id,
        UUID revisionItem,
        String observacion,
        String estadoObservacionRevision,
        String estadoObservacionRevisionNombre
) {
}
