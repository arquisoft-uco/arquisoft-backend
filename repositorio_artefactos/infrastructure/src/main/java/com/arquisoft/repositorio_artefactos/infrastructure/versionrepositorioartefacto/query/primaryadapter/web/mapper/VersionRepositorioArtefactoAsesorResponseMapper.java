package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.RepositorioDeVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto.RepositorioDeVersionResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto.VersionRepositorioArtefactoAsesorResponseDTO;

public final class VersionRepositorioArtefactoAsesorResponseMapper {

    private VersionRepositorioArtefactoAsesorResponseMapper() {}

    public static VersionRepositorioArtefactoAsesorResponseDTO toResponse(
            VersionRepositorioArtefactoAsesorReadModel readModel) {
        return new VersionRepositorioArtefactoAsesorResponseDTO(
                readModel.id(),
                readModel.nombreDocumento(),
                readModel.numeroVersion(),
                readModel.fechaCreacion(),
                readModel.tipoContenido(),
                readModel.tamanoBytes(),
                readModel.nombreArchivo(),
                readModel.hashSha256(),
                toResponse(readModel.repositorioArtefacto()));
    }

    private static RepositorioDeVersionResponseDTO toResponse(RepositorioDeVersionReadModel readModel) {
        return new RepositorioDeVersionResponseDTO(
                readModel.id(),
                readModel.nombreRepositorio(),
                readModel.descripcion());
    }
}
