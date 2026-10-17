package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class MapaRutaSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : MapaRutaCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case FECHA_INICIO -> "fechaInicio";
                case FECHA_FIN    -> "fechaFin";
                case COORDINADOR  -> null; // no ordenable
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private MapaRutaSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
