package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ObservacionItemAsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : ObservacionItemAsesorCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case DESCRIPCION -> "descripcion";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ObservacionItemAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
