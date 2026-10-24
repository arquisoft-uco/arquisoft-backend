package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.dto.EstudianteArtefactoResponseDTO;

public final class EstudianteArtefactoResponseMapper {

    private EstudianteArtefactoResponseMapper() {}

    public static EstudianteArtefactoResponseDTO toResponse(EstudianteArtefactoReadModel readModel) {
        return new EstudianteArtefactoResponseDTO(
                readModel.id(),
                readModel.artefactoId(),
                readModel.estudianteId(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.vigente());
    }
}
