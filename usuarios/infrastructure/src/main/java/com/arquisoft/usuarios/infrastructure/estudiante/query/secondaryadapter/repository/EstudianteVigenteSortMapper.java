package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EstudianteVigenteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (EstudianteVigenteCriteria.Campo campo : EstudianteVigenteCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EstudianteVigenteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
