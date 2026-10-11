package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ObservacionItemSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        var m = new LinkedHashMap<String, String>();
        for (var campo : ObservacionItemCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case REVISION_ITEM               -> null;
                case ESTADO_OBSERVACION_REVISION -> "estadoOrden";
                case ASESOR_ID                   -> null;
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ObservacionItemSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
