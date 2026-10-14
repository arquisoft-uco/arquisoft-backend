package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class ProyectoGradoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (ProyectoGradoCriteria.Campo campo : ProyectoGradoCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case TITULO_PROYECTO -> "tituloProyecto";
                case ID, ESTADO_PROYECTO_GRADO, COORDINADOR -> null;
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private ProyectoGradoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
