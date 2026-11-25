package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ParticipacionArtefactoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : ParticipacionArtefactoCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case OMITIDO, PROYECTO_FINALIZADO, PROYECTO_CANCELADO, ESTUDIANTE_ID -> null;
                case NOMBRE_PROYECTO        -> "nombreProyecto";
                case NOMBRE_DOCUMENTO       -> "nombreDocumento";
                case VERSION_DOCUMENTO      -> "versionDocumento";
                case NOMBRE_REPOSITORIO     -> "nombreRepositorio";
                case NOMBRE_ASESOR          -> "nombreAsesor";
                case ESTADO_ULTIMA_REVISION -> "estadoUltimaRevision";
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ParticipacionArtefactoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
