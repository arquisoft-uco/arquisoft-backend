package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ArtefactoEstudianteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : ArtefactoEstudianteCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case PROYECTO_GRADO, VERSION_REPOSITORIO_ARTEFACTO, ASESOR, ESTUDIANTE_ID -> null;
                case NOMBRE_PROYECTO    -> "nombreProyecto";
                case NOMBRE_DOCUMENTO   -> "nombreDocumento";
                case VERSION_DOCUMENTO  -> "versionDocumento";
                case NOMBRE_REPOSITORIO -> "nombreRepositorio";
                case NOMBRE_ASESOR      -> "nombreAsesor";
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ArtefactoEstudianteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
