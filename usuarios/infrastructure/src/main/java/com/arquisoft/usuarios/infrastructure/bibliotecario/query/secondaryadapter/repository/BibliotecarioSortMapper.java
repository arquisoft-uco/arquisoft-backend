package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class BibliotecarioSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : BibliotecarioCriteria.Campo.values()) {
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

    private BibliotecarioSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
