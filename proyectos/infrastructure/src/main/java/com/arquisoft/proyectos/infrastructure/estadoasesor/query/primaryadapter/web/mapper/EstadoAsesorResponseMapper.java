package com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web.dto.EstadoAsesorResponseDTO;

public final class EstadoAsesorResponseMapper {

    private EstadoAsesorResponseMapper() {}

    public static EstadoAsesorResponseDTO toResponse(EstadoAsesorReadModel readModel) {
        return new EstadoAsesorResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
