package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(EvaluacionCriteria.Campo.PROYECTO.getClave(), "entregableProyecto");
        m.put(EvaluacionCriteria.Campo.ESTADO.getClave(), "estadoNombre");
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
