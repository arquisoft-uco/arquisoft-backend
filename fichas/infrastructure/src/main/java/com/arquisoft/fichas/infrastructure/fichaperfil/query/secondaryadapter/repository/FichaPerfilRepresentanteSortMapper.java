package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class FichaPerfilRepresentanteSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        var m = new LinkedHashMap<String, String>();
        for (var campo : FichaPerfilRepresentanteCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case TITULO_PROYECTO -> "tituloProyecto";
                case ASESOR_NOMBRE   -> "asesorNombre";
                case ASESOR_EMAIL    -> "asesorEmail";
                case ESTADO_FICHA    -> null;
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private FichaPerfilRepresentanteSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
