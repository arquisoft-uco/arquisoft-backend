package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.dto.EstudianteAsesoradoProyectoGradoResponseDTO;

public final class EstudianteAsesoradoProyectoGradoResponseMapper {

    private EstudianteAsesoradoProyectoGradoResponseMapper() {}

    public static EstudianteAsesoradoProyectoGradoResponseDTO toResponse(
            EstudianteAsesoradoProyectoGradoReadModel readModel) {
        return new EstudianteAsesoradoProyectoGradoResponseDTO(
                readModel.estudianteId(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email());
    }
}
