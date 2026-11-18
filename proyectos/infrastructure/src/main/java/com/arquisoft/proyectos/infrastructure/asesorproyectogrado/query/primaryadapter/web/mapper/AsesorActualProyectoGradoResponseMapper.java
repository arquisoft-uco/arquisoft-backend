package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.dto.AsesorActualProyectoGradoResponseDTO;

public final class AsesorActualProyectoGradoResponseMapper {

    private AsesorActualProyectoGradoResponseMapper() {}

    public static AsesorActualProyectoGradoResponseDTO toResponse(AsesorActualProyectoGradoReadModel readModel) {
        return new AsesorActualProyectoGradoResponseDTO(
                readModel.proyectoGradoId(),
                readModel.tituloProyecto(),
                readModel.asesorId(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email());
    }
}
