package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class PlanRealProyectoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : PlanRealProyectoCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case TIEMPO_REAL_DIAS -> "tiempoRealDias";
                case DESCRIPCION_REAL -> "descripcionReal";
                case DESCRIPCION_TAREA -> "descripcionTarea";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private PlanRealProyectoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
