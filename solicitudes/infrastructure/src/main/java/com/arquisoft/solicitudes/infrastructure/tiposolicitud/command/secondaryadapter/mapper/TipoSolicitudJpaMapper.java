package com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.infrastructure.tiposolicitud.command.secondaryadapter.entity.TipoSolicitudJpaEntity;

public final class TipoSolicitudJpaMapper {

    private TipoSolicitudJpaMapper() {}

    public static TipoSolicitudJpaEntity toReferencia(String id) {
        return TipoSolicitudJpaEntity.builder().id(id).build();
    }
}
