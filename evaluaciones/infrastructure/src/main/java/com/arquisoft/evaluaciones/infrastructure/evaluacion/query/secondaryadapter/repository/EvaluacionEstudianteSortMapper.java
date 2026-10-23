package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionEstudianteSortMapper {

    static final String RUTA_DESEMPATE = "id";

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(EvaluacionEstudianteCriteria.Campo.PROYECTO.getClave(), "entregableProyecto");
        m.put(EvaluacionEstudianteCriteria.Campo.ESTADO.getClave(), "estadoNombre");
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionEstudianteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
