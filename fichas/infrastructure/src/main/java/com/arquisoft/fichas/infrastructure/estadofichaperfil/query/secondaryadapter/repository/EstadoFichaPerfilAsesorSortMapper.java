package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class EstadoFichaPerfilAsesorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (EstadoFichaPerfilAsesorCriteria.Campo campo : EstadoFichaPerfilAsesorCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case TITULO_PROYECTO -> "tituloProyecto";
                case FICHA_PERFIL, ESTADO_FICHA, ASESOR_FICHA -> null; // no ordenables
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private EstadoFichaPerfilAsesorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
