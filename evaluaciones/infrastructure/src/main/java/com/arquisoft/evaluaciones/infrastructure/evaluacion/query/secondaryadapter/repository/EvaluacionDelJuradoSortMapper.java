package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionDelJuradoSortMapper {

    static final String RUTA_DESEMPATE = "id";

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(EvaluacionDelJuradoCriteria.Campo.PROYECTO.getClave(), "entregableProyecto");
        m.put(EvaluacionDelJuradoCriteria.Campo.ESTADO.getClave(), "estadoNombre");
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionDelJuradoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
