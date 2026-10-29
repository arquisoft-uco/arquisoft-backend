package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto.ArtefactoEstudianteResponseDTO;

public final class ArtefactoEstudianteResponseMapper {

    private ArtefactoEstudianteResponseMapper() {}

    public static ArtefactoEstudianteResponseDTO toResponse(ArtefactoEstudianteReadModel readModel) {
        return new ArtefactoEstudianteResponseDTO(
                readModel.id(),
                readModel.proyectoGrado(),
                readModel.nombreProyecto(),
                readModel.versionRepositorioArtefacto(),
                readModel.nombreDocumento(),
                readModel.versionDocumento(),
                readModel.nombreRepositorio(),
                readModel.asesor(),
                readModel.nombreAsesor());
    }
}
