package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.mapper;

import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;

public final class ConsultarTiposSolicitudMapper {

    private ConsultarTiposSolicitudMapper() {}

    public static TipoSolicitudCriteria toCriteria(ConsultarTiposSolicitudQuery query) {
        return new TipoSolicitudCriteria(query.tipos());
    }
}
