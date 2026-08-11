package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class FichaPerfilSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (FichaPerfilCriteria.Campo campo : FichaPerfilCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case TITULO_PROYECTO -> "tituloProyecto";
                case ASESOR_NOMBRE   -> "asesorFicha.nombre";
                case ASESOR_EMAIL    -> "asesorFicha.email";
                case ASESOR_ID       -> null; // no ordenable
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private FichaPerfilSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
