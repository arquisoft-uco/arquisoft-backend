package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarInformacionTareaQuery;

public final class ConsultarInformacionTareaMapper {

    private ConsultarInformacionTareaMapper() {}

    public static TareaCriteria toCriteria(ConsultarInformacionTareaQuery query) {
        return new TareaCriteria(query.tarea());
    }
}
