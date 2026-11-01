package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto.ArtefactoSinAvanceResponseDTO;

public final class ArtefactoSinAvanceResponseMapper {

    private ArtefactoSinAvanceResponseMapper() {}

    public static ArtefactoSinAvanceResponseDTO toResponse(ArtefactoSinAvanceReadModel readModel) {
        return new ArtefactoSinAvanceResponseDTO(
                readModel.id(),
                readModel.proyectoGrado(),
                readModel.nombreProyecto(),
                readModel.versionRepositorioArtefacto(),
                readModel.nombreDocumento(),
                readModel.versionDocumento(),
                readModel.nombreRepositorio(),
                readModel.fechaUltimaVersion(),
                readModel.diasSinAvance(),
                readModel.estadoUltimaRevision());
    }
}
