package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository.mapper;

import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository.RespuestaJpaQueryEntity;

public final class RespuestaQueryMapper {

    private RespuestaQueryMapper() {}

    public static RespuestaReadModel toReadModel(RespuestaJpaQueryEntity entity) {
        return new RespuestaReadModel(
                entity.getId(),
                entity.getContenido(),
                entity.getFechaRespuesta(),
                entity.getEstadoRespuestaId(),
                entity.getEstadoRespuestaNombre(),
                new SolicitudReadModel(
                        entity.getSolicitudId(),
                        entity.getMensajeSolicitud(),
                        entity.getFechaCreacion(),
                        entity.getTipoSolicitudId(),
                        entity.getTipoSolicitudNombre(),
                        new RemitenteReadModel(
                                entity.getRemitenteUsuarioId(),
                                entity.getRemitenteIdentificador(),
                                entity.getRemitenteNombre(),
                                entity.getRemitenteEmail()),
                        new DestinatarioReadModel(
                                entity.getDestinatarioUsuarioId(),
                                entity.getDestinatarioIdentificador(),
                                entity.getDestinatarioNombre(),
                                entity.getDestinatarioEmail())));
    }
}
