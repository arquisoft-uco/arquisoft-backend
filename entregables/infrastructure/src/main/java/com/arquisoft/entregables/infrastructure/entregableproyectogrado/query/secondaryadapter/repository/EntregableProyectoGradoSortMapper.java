package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EntregableProyectoGradoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        var m = new LinkedHashMap<String, String>();
        for (var campo : EntregableProyectoGradoCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case PROYECTO_GRADO_NOMBRE -> "proyectoGradoNombre";
                case ASESOR_NOMBRE -> "asesorNombre";
                case VERSION_ENTREGABLE -> "versionEntregable";
                case PROYECTO_GRADO_ID, ASESOR_ID, ESTADO_ENTREGABLE -> null;
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EntregableProyectoGradoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
