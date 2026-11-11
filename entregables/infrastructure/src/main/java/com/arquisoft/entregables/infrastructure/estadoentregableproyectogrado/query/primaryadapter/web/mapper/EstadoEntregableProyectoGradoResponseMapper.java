package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web.dto.EstadoEntregableProyectoGradoResponseDTO;

public final class EstadoEntregableProyectoGradoResponseMapper {

    private EstadoEntregableProyectoGradoResponseMapper() {}

    public static EstadoEntregableProyectoGradoResponseDTO toResponse(
            EstadoEntregableProyectoGradoReadModel readModel) {
        return new EstadoEntregableProyectoGradoResponseDTO(
                readModel.id(),
                readModel.estadoEntregable(),
                readModel.estadoEntregableNombre(),
                readModel.fechaActualizacion());
    }
}
