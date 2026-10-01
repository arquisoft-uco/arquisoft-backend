package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RevisionItemResponseDTO(
        UUID id,
        UUID item,
        String estadoRevision,
        String estadoRevisionNombre,
        Instant fechaCreacion
) {
}
