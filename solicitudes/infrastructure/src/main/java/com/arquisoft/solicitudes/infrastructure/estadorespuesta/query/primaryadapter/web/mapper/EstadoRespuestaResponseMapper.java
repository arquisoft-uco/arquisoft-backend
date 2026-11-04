package com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.primaryadapter.web.dto.EstadoRespuestaResponseDTO;

public final class EstadoRespuestaResponseMapper {

    private EstadoRespuestaResponseMapper() {}

    public static EstadoRespuestaResponseDTO toResponse(EstadoRespuestaReadModel readModel) {
        return new EstadoRespuestaResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
