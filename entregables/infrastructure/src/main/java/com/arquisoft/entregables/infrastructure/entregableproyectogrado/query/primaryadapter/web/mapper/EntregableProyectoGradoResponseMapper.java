package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.EntregableProyectoGradoResponseDTO;

public final class EntregableProyectoGradoResponseMapper {

    private EntregableProyectoGradoResponseMapper() {}

    public static EntregableProyectoGradoResponseDTO toResponse(EntregableProyectoGradoReadModel readModel) {
        return new EntregableProyectoGradoResponseDTO(
                readModel.id(),
                readModel.versionEntregable(),
                new EntregableProyectoGradoResponseDTO.ProyectoGradoResponseDTO(
                        readModel.proyectoGrado().id(),
                        readModel.proyectoGrado().nombre()),
                new EntregableProyectoGradoResponseDTO.AsesorResponseDTO(
                        readModel.asesor().id(),
                        readModel.asesor().nombre(),
                        readModel.asesor().email()),
                new EntregableProyectoGradoResponseDTO.EstadoEntregableResponseDTO(
                        readModel.estadoEntregable().id(),
                        readModel.estadoEntregable().nombre()));
    }
}
