package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarInformacionTareaQuery;

import java.util.UUID;

public final class ConsultarInformacionTareaRequestMapper {

    private ConsultarInformacionTareaRequestMapper() {}

    public static ConsultarInformacionTareaQuery toQuery(UUID tarea) {
        return ConsultarInformacionTareaQuery.crear(tarea);
    }
}
