package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionAsignadaJuradoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(EvaluacionesAsignadasJuradoCriteria.Campo.ESTADO_JURADO.getClave(), "estadoJuradoOrden");
        m.put(EvaluacionesAsignadasJuradoCriteria.Campo.ESTADO.getClave(), "estadoOrden");
        m.put(EvaluacionesAsignadasJuradoCriteria.Campo.PROYECTO.getClave(), "entregableProyecto");
        m.put(EvaluacionesAsignadasJuradoCriteria.Campo.VERSION.getClave(), "entregableVersion");
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionAsignadaJuradoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
