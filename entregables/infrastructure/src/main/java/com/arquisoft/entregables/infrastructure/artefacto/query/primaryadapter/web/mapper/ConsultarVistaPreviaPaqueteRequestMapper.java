package com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.artefacto.query.primaryport.model.ConsultarVistaPreviaPaqueteQuery;

import java.util.UUID;

public final class ConsultarVistaPreviaPaqueteRequestMapper {

    private ConsultarVistaPreviaPaqueteRequestMapper() {}

    public static ConsultarVistaPreviaPaqueteQuery toQuery(UUID proyectoGrado) {
        return ConsultarVistaPreviaPaqueteQuery.crear(proyectoGrado);
    }
}
