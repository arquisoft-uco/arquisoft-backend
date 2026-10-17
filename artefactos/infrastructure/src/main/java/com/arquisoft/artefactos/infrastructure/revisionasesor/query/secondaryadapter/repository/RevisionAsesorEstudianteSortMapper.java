package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class RevisionAsesorEstudianteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : RevisionAsesorEstudianteCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case VERSION_ARTEFACTO      -> null;
                case ESTADO_REVISION_ASESOR -> "estadoRevisionAsesorNombre";
                case ESTUDIANTE_ID          -> null;
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private RevisionAsesorEstudianteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
