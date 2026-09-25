package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class CoordinadorVigenteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (CoordinadorVigenteCriteria.Campo campo : CoordinadorVigenteCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private CoordinadorVigenteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
