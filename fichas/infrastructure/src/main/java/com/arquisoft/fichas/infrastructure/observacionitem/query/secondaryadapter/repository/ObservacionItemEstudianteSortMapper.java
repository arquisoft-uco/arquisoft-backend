package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ObservacionItemEstudianteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        var m = new LinkedHashMap<String, String>();
        for (var campo : ObservacionItemEstudianteCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case REVISION_ITEM               -> null;
                case ESTADO_OBSERVACION_REVISION -> "estadoOrden";
                case ESTUDIANTE_ID               -> null;
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ObservacionItemEstudianteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
