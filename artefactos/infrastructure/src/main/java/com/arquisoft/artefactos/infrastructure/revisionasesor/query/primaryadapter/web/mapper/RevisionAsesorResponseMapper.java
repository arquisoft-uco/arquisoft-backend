package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.dto.RevisionAsesorResponseDTO;

public final class RevisionAsesorResponseMapper {

    private RevisionAsesorResponseMapper() {}

    public static RevisionAsesorResponseDTO toResponse(RevisionAsesorReadModel readModel) {
        return new RevisionAsesorResponseDTO(
                readModel.id(),
                readModel.versionArtefacto(),
                readModel.artefacto(),
                readModel.version(),
                readModel.estadoRevisionAsesor(),
                readModel.estadoRevisionAsesorNombre());
    }
}
