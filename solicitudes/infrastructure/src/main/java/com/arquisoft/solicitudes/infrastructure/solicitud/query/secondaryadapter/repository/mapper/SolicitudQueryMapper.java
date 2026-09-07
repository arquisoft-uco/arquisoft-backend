package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository.mapper;

import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository.SolicitudJpaQueryEntity;

public final class SolicitudQueryMapper {

    private SolicitudQueryMapper() {}

    public static SolicitudReadModel toReadModel(SolicitudJpaQueryEntity entity) {
        return new SolicitudReadModel(
                entity.getId(),
                entity.getMensajeSolicitud(),
                entity.getFechaCreacion(),
                entity.getTipoSolicitudId(),
                entity.getTipoSolicitudNombre(),
                new RemitenteReadModel(
                        entity.getRemitenteUsuarioId(),
                        entity.getRemitenteIdentificador(),
                        entity.getRemitenteNombre(),
                        entity.getRemitenteEmail()));
    }
}
