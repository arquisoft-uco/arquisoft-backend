package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EvaluacionFichaPerfilAsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (EvaluacionFichaPerfilAsesorCriteria.Campo campo : EvaluacionFichaPerfilAsesorCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case TITULO_PROYECTO -> "tituloProyecto";
                case FECHA_CREACION -> "fechaCreacion";
                case FICHA_PERFIL, ESTADO_EVALUACION, REPRESENTANTE_COMITE, ASESOR_FICHA -> null; // no ordenables
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EvaluacionFichaPerfilAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
