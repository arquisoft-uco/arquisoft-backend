package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.dto.ParticipacionArtefactoResponseDTO;

public final class ParticipacionArtefactoResponseMapper {

    private ParticipacionArtefactoResponseMapper() {}

    public static ParticipacionArtefactoResponseDTO toResponse(ParticipacionArtefactoReadModel readModel) {
        return new ParticipacionArtefactoResponseDTO(
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
