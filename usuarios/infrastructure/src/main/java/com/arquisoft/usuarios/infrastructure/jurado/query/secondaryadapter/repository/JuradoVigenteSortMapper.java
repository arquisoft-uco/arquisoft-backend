package com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class JuradoVigenteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : JuradoVigenteCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
                case ESTADO        -> null;
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private JuradoVigenteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
