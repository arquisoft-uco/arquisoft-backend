package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.dto.RevisionAsesorAsesorResponseDTO;

public final class RevisionAsesorAsesorResponseMapper {

    private RevisionAsesorAsesorResponseMapper() {}

    public static RevisionAsesorAsesorResponseDTO toResponse(RevisionAsesorAsesorReadModel readModel) {
        return new RevisionAsesorAsesorResponseDTO(
                readModel.id(),
                readModel.versionArtefacto(),
                readModel.artefacto(),
                readModel.version(),
                readModel.estadoRevisionAsesor(),
                readModel.estadoRevisionAsesorNombre(),
                readModel.fechaCreacion());
    }
}
