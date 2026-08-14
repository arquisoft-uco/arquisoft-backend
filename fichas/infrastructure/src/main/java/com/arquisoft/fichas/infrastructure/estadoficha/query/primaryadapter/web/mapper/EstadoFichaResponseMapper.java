package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.dto.EstadoFichaResponseDTO;

public final class EstadoFichaResponseMapper {

    private EstadoFichaResponseMapper() {}

    public static EstadoFichaResponseDTO toResponse(EstadoFichaReadModel readModel) {
        return new EstadoFichaResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
