package com.arquisoft.solicitudes.application.respuesta.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;

public final class RespuestaMapper {

    private RespuestaMapper() {}

    public static RespuestaEntity toEntity(RespuestaDomain domain) {
        return new RespuestaEntity(
                domain.getId(),
                domain.getSolicitud(),
                domain.getFechaRespuesta(),
                domain.getContenido(),
                domain.getEstadoRespuesta().getId());
    }

    public static RespuestaDomain toDomain(RespuestaEntity entity) {
        return RespuestaDomain.reconstruir(
                entity.id(),
                entity.solicitud(),
                entity.fechaRespuesta(),
                entity.contenido(),
                EstadoRespuesta.desde(entity.estadoRespuesta()));
    }
}
