package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EstudianteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (EstudianteCriteria.Campo campo : EstudianteCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
                case ESTADO, VIGENTE -> null;
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EstudianteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
