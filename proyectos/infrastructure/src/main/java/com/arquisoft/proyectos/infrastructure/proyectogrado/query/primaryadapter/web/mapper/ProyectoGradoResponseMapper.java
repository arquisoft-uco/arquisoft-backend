package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.estadoproyecto.query.primaryadapter.web.dto.EstadoProyectoResponseDTO;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto.ProyectoGradoResponseDTO;

public final class ProyectoGradoResponseMapper {

    private ProyectoGradoResponseMapper() {}

    public static ProyectoGradoResponseDTO toResponse(ProyectoGradoReadModel readModel) {
        return new ProyectoGradoResponseDTO(
                readModel.id(),
                readModel.fichaPerfil(),
                readModel.tituloProyecto(),
                new EstadoProyectoResponseDTO(
                        readModel.estado().id(),
                        readModel.estado().nombre()));
    }
}
