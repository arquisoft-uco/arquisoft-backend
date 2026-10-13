package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.dto.EstadoProyectoGradoResponseDTO;

public final class EstadoProyectoGradoResponseMapper {

    private EstadoProyectoGradoResponseMapper() {}

    public static EstadoProyectoGradoResponseDTO toResponse(EstadoProyectoGradoReadModel readModel) {
        return new EstadoProyectoGradoResponseDTO(
                readModel.id(),
                readModel.estadoProyecto(),
                readModel.estadoProyectoNombre(),
                readModel.fechaActualizacion());
    }
}
