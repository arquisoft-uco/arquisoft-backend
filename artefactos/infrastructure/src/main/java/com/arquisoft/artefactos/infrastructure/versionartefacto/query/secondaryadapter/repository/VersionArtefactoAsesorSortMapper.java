package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class VersionArtefactoAsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : VersionArtefactoAsesorCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case ARTEFACTO      -> null;
                case PROYECTO_GRADO   -> null;
                case NOMBRE_PROYECTO  -> null;
                case NOMBRE_DOCUMENTO -> null;
                case VERSION        -> "version";
                case FECHA_CREACION -> "fechaCreacion";
                case ASESOR_ID      -> null;
                case ESTADO_REVISION -> "ordenEstadoRevision";
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private VersionArtefactoAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
