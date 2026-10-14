package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.dto.EstudianteProyectoGradoResponseDTO;

public final class EstudianteProyectoGradoResponseMapper {

    private EstudianteProyectoGradoResponseMapper() {}

    public static EstudianteProyectoGradoResponseDTO toResponse(EstudianteProyectoGradoReadModel readModel) {
        return new EstudianteProyectoGradoResponseDTO(
                readModel.id(),
                readModel.proyectoGradoId(),
                readModel.estudianteId(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.vigente());
    }
}
