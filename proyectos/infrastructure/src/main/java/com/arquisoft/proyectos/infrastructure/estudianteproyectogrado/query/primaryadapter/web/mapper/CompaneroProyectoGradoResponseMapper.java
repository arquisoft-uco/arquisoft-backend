package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.dto.CompaneroProyectoGradoResponseDTO;

public final class CompaneroProyectoGradoResponseMapper {

    private CompaneroProyectoGradoResponseMapper() {}

    public static CompaneroProyectoGradoResponseDTO toResponse(CompaneroProyectoGradoReadModel readModel) {
        return new CompaneroProyectoGradoResponseDTO(
                readModel.estudianteId(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email());
    }
}
