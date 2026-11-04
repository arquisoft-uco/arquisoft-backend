package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.mapper;

import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudParaRecibirCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudParaRecibirQuery;

public final class ConsultarTiposSolicitudParaRecibirMapper {

    private ConsultarTiposSolicitudParaRecibirMapper() {}

    public static TipoSolicitudParaRecibirCriteria toCriteria(ConsultarTiposSolicitudParaRecibirQuery query) {
        return new TipoSolicitudParaRecibirCriteria(query.actor(), query.rolesIdentidad());
    }
}
