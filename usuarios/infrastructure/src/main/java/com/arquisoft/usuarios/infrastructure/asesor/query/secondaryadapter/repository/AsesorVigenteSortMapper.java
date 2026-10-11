package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class AsesorVigenteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (AsesorVigenteCriteria.Campo campo : AsesorVigenteCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
                case ESTADO        -> null;
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private AsesorVigenteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
