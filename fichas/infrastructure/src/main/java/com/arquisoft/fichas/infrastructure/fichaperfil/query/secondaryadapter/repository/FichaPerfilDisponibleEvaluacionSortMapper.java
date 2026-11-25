package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class FichaPerfilDisponibleEvaluacionSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        var m = new LinkedHashMap<String, String>();
        for (var campo : FichaPerfilDisponibleEvaluacionCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case TITULO_PROYECTO -> "tituloProyecto";
                case ASESOR_NOMBRE   -> "asesorNombre";
                case ASESOR_EMAIL    -> "asesorEmail";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private FichaPerfilDisponibleEvaluacionSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
