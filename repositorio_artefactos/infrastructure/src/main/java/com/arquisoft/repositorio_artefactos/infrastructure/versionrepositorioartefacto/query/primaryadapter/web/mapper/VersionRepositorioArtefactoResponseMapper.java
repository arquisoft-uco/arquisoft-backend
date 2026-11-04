package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto.VersionRepositorioArtefactoResponseDTO;

public final class VersionRepositorioArtefactoResponseMapper {

    private VersionRepositorioArtefactoResponseMapper() {}

    public static VersionRepositorioArtefactoResponseDTO toResponse(VersionRepositorioArtefactoReadModel readModel) {
        return new VersionRepositorioArtefactoResponseDTO(
                readModel.id(),
                readModel.nombreDocumento(),
                readModel.numeroVersion(),
                readModel.fechaCreacion(),
                readModel.tipoContenido(),
                readModel.tamanoBytes(),
                readModel.nombreArchivo(),
                readModel.hashSha256());
    }
}
