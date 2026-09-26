package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class AsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (AsesorCriteria.Campo campo : AsesorCriteria.Campo.values()) {
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

    private AsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
