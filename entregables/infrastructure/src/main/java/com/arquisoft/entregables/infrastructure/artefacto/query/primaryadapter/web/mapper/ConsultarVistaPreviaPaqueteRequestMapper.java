package com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.artefacto.query.primaryport.model.ConsultarVistaPreviaPaqueteQuery;
import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.util.UUID;

public final class ConsultarVistaPreviaPaqueteRequestMapper {

    private ConsultarVistaPreviaPaqueteRequestMapper() {}

    public static ConsultarVistaPreviaPaqueteQuery toQuery(UUID proyectoGrado, IdentidadActual identidad) {
        return ConsultarVistaPreviaPaqueteQuery.crear(proyectoGrado, identidad.id());
    }
}
