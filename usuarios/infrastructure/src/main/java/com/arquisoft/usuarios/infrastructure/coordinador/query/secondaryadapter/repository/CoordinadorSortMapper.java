package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class CoordinadorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (CoordinadorCriteria.Campo campo : CoordinadorCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
                case ESTADO, VIGENTE -> null; // no ordenables
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private CoordinadorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
