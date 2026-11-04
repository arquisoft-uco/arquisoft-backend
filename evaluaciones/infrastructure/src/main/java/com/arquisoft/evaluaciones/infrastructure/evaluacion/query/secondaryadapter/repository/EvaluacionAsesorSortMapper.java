package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionAsesorSortMapper {

    static final String RUTA_DESEMPATE = "id";

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(EvaluacionAsesorCriteria.Campo.PROYECTO.getClave(), "entregableProyecto");
        m.put(EvaluacionAsesorCriteria.Campo.ESTADO.getClave(), "estadoNombre");
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
