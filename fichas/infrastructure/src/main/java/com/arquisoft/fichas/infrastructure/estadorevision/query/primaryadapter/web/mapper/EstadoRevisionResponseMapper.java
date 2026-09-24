package com.arquisoft.fichas.infrastructure.estadorevision.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.infrastructure.estadorevision.query.primaryadapter.web.dto.EstadoRevisionResponseDTO;

public final class EstadoRevisionResponseMapper {

    private EstadoRevisionResponseMapper() {}

    public static EstadoRevisionResponseDTO toResponse(EstadoRevisionReadModel readModel) {
        return new EstadoRevisionResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
