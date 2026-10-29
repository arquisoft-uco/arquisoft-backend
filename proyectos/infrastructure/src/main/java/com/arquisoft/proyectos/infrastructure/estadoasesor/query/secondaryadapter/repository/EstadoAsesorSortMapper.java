package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EstadoAsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : EstadoAsesorCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case ID     -> "id";
                case NOMBRE -> "nombre";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EstadoAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
