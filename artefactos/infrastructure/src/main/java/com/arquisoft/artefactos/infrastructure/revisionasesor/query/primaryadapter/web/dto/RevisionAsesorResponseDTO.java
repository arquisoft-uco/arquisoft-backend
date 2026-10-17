package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RevisionAsesorResponseDTO(
        UUID id,
        UUID versionArtefacto,
        UUID artefacto,
        int version,
        String estadoRevisionAsesor,
        String estadoRevisionAsesorNombre
) {
}
