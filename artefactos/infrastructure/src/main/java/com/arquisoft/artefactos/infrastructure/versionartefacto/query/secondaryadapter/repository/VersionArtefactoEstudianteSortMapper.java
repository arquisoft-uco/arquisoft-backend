package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class VersionArtefactoEstudianteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : VersionArtefactoEstudianteCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case ARTEFACTO      -> null;
                case VERSION        -> "version";
                case FECHA_CREACION -> "fechaCreacion";
                case ESTUDIANTE_ID      -> null;
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private VersionArtefactoEstudianteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
