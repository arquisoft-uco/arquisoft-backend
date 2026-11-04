package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ArtefactoSinAvanceSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : ArtefactoSinAvanceCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case PROYECTO_GRADO, VERSION_REPOSITORIO_ARTEFACTO -> null;
                case NOMBRE_PROYECTO         -> "nombreProyecto";
                case NOMBRE_DOCUMENTO        -> "nombreDocumento";
                case VERSION_DOCUMENTO       -> "versionDocumento";
                case NOMBRE_REPOSITORIO      -> "nombreRepositorio";
                case ESTADO_ULTIMA_REVISION  -> "estadoUltimaRevision";
                case FECHA_ULTIMA_VERSION    -> "fechaUltimaVersion";
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ArtefactoSinAvanceSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
