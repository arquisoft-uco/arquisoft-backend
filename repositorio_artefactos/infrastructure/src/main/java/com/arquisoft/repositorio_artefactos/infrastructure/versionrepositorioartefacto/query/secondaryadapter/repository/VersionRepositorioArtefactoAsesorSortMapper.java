package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class VersionRepositorioArtefactoAsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : VersionRepositorioArtefactoAsesorCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case NOMBRE_DOCUMENTO -> "nombreDocumento";
                case NUMERO_VERSION -> "numeroVersion";
                case FECHA_CREACION -> "fechaCreacion";
                case NOMBRE_REPOSITORIO -> "nombreRepositorio";
                case TIPO_CONTENIDO -> null;
                case NOMBRE_ARCHIVO -> null;
                case REPOSITORIO_ID -> null;
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private VersionRepositorioArtefactoAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
