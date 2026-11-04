package com.arquisoft.entregables.infrastructure.estadoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.entregables.infrastructure.estadoentregable.query.primaryadapter.web.dto.EstadoEntregableResponseDTO;

public final class EstadoEntregableResponseMapper {

    private EstadoEntregableResponseMapper() {}

    public static EstadoEntregableResponseDTO toResponse(EstadoEntregableReadModel readModel) {
        return new EstadoEntregableResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
