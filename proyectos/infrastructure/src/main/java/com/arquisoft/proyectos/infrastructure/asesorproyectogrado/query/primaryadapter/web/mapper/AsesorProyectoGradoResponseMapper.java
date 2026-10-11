package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.dto.AsesorProyectoGradoResponseDTO;

public final class AsesorProyectoGradoResponseMapper {

    private AsesorProyectoGradoResponseMapper() {}

    public static AsesorProyectoGradoResponseDTO toResponse(AsesorProyectoGradoReadModel readModel) {
        return new AsesorProyectoGradoResponseDTO(
                readModel.id(),
                readModel.proyectoGradoId(),
                readModel.asesorId(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.estadoAsesor(),
                readModel.estadoAsesorNombre(),
                readModel.fechaAsignado(),
                readModel.vigente());
    }
}
