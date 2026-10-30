package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto.ArtefactoResponseDTO;

public final class ArtefactoResponseMapper {

    private ArtefactoResponseMapper() {}

    public static ArtefactoResponseDTO toResponse(ArtefactoReadModel readModel) {
        return new ArtefactoResponseDTO(
                readModel.id(),
                readModel.proyectoGrado(),
                readModel.nombreProyecto(),
                readModel.versionRepositorioArtefacto(),
                readModel.nombreDocumento(),
                readModel.versionDocumento(),
                readModel.nombreRepositorio(),
                readModel.omitido());
    }
}
