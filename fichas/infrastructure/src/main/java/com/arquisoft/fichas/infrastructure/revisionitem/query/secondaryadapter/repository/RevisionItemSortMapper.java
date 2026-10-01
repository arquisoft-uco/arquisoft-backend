package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class RevisionItemSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (RevisionItemCriteria.Campo campo : RevisionItemCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case ITEM            -> null; // no ordenable
                case ESTADO_REVISION -> "estadoRevisionNombre";
                case ASESOR_ID       -> null; // no ordenable
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private RevisionItemSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
