package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.dto.VersionArtefactoResponseDTO;

public final class VersionArtefactoResponseMapper {

    private VersionArtefactoResponseMapper() {}

    public static VersionArtefactoResponseDTO toResponse(VersionArtefactoReadModel readModel) {
        return new VersionArtefactoResponseDTO(
                readModel.id(),
                readModel.artefacto(),
                readModel.version(),
                readModel.fechaCreacion(),
                readModel.uriArchivo(),
                readModel.nombreProyecto(),
                readModel.nombreDocumento(),
                readModel.versionDocumento(),
                readModel.nombreRepositorio(),
                readModel.nombreAsesor());
    }
}
