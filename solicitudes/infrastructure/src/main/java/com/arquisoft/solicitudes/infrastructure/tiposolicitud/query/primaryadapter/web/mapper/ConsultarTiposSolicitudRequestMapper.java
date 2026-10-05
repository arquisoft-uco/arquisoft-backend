package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;

import java.util.Set;

public final class ConsultarTiposSolicitudRequestMapper {

    private ConsultarTiposSolicitudRequestMapper() {}

    public static ConsultarTiposSolicitudQuery toQuery(Set<String> tipos) {
        return ConsultarTiposSolicitudQuery.crear(tipos);
    }
}
