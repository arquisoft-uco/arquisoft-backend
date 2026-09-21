package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionJuradoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(EvaluacionJuradoCriteria.Campo.JURADO.getClave(), "juradoNombre");
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionJuradoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
