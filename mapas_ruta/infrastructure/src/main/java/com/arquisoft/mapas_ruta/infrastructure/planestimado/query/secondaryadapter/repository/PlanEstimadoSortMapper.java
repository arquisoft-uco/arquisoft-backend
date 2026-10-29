package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class PlanEstimadoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : PlanEstimadoTareaCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case TIEMPO_ESTIMADO_DIAS -> "tiempoEstimadoDias";
                case DESCRIPCION_ESTIMADO -> "descripcionEstimado";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private PlanEstimadoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
