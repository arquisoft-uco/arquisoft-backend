package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class RepresentanteComiteVigenteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (RepresentanteComiteVigenteCriteria.Campo campo : RepresentanteComiteVigenteCriteria.Campo.values()) {
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

    private RepresentanteComiteVigenteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
