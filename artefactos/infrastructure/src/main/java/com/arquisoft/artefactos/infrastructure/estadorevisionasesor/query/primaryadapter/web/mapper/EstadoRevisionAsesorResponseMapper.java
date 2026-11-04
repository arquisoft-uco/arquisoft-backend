package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.primaryadapter.web.dto.EstadoRevisionAsesorResponseDTO;

public final class EstadoRevisionAsesorResponseMapper {

    private EstadoRevisionAsesorResponseMapper() {}

    public static EstadoRevisionAsesorResponseDTO toResponse(EstadoRevisionAsesorReadModel readModel) {
        return new EstadoRevisionAsesorResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
