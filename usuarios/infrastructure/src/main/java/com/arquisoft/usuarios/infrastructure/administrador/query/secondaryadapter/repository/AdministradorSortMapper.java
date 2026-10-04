package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class AdministradorSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : AdministradorCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
                case ESTADO, VIGENTE -> null;
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private AdministradorSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
