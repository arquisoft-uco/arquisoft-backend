package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class FichaPerfilSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (FichaPerfilCriteria.CampoOrden campo : FichaPerfilCriteria.CampoOrden.values()) {
            String ruta = switch (campo) {
                case TITULO_PROYECTO -> "tituloProyecto";
                case ASESOR_NOMBRE   -> "asesorFicha.nombre";
                case ASESOR_EMAIL    -> "asesorFicha.email";
            };
            m.put(campo.getClave(), ruta);
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private FichaPerfilSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
