package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class TareaSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : TareaMapaRutaCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case DESCRIPCION -> "descripcion";
                case ESTADO_ITEM -> "estadoItemId";
                case ASESOR      -> null; // no ordenable
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private TareaSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
