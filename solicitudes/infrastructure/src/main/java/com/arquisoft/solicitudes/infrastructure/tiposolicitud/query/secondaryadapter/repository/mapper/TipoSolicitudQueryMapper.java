package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository.mapper;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository.TipoSolicitudJpaQueryEntity;

public final class TipoSolicitudQueryMapper {

    private TipoSolicitudQueryMapper() {}

    public static TipoSolicitudReadModel toReadModel(TipoSolicitudJpaQueryEntity entity) {
        return new TipoSolicitudReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
