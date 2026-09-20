package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.dto.RespuestaResponseDTO;
import com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper.SolicitudResponseMapper;

public final class RespuestaResponseMapper {

    private RespuestaResponseMapper() {}

    public static RespuestaResponseDTO toResponse(RespuestaReadModel readModel) {
        return new RespuestaResponseDTO(
                readModel.id(),
                readModel.contenido(),
                readModel.fechaRespuesta(),
                readModel.estadoRespuestaId(),
                readModel.estadoRespuestaNombre(),
                SolicitudResponseMapper.toResponse(readModel.solicitud()));
    }
}
