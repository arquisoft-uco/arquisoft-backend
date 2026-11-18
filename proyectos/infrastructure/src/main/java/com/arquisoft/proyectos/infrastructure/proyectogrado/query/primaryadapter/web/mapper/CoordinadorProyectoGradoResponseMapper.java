package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto.CoordinadorProyectoGradoResponseDTO;

public final class CoordinadorProyectoGradoResponseMapper {

    private CoordinadorProyectoGradoResponseMapper() {}

    public static CoordinadorProyectoGradoResponseDTO toResponse(CoordinadorProyectoGradoReadModel readModel) {
        return new CoordinadorProyectoGradoResponseDTO(
                readModel.proyectoGradoId(),
                readModel.tituloProyecto(),
                readModel.coordinadorId(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email());
    }
}
