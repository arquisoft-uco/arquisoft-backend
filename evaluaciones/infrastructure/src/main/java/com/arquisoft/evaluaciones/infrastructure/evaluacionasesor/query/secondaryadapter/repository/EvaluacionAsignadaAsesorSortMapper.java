package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionAsignadaAsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(EvaluacionesAsignadasAsesorCriteria.Campo.ESTADO.getClave(), "estadoOrden");
        m.put(EvaluacionesAsignadasAsesorCriteria.Campo.PROYECTO.getClave(), "entregableProyecto");
        m.put(EvaluacionesAsignadasAsesorCriteria.Campo.VERSION.getClave(), "entregableVersion");
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionAsignadaAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
