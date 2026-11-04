package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ObservacionItemJuradoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (ObservacionItemJuradoCriteria.Campo campo : ObservacionItemJuradoCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case DESCRIPCION -> "descripcion";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ObservacionItemJuradoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
