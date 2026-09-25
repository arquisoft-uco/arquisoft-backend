package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class AsesorFichaVigenteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (AsesorFichaVigenteCriteria.Campo campo : AsesorFichaVigenteCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
                case ESTADO        -> null;
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private AsesorFichaVigenteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
