package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class UsuarioSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (UsuarioCriteria.Campo campo : UsuarioCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case IDENTIFICADOR -> "identificador";
                case NOMBRE        -> "nombre";
                case EMAIL         -> "email";
                case CONTACTO, ESTADO, VIGENTE, ES_ESTUDIANTE, ES_ASESOR, ES_ASESOR_FICHA, ES_COORDINADOR -> null;
                // TODO HU233, HU242, HU252, HU255: sumar ES_ADMINISTRADOR, ES_BIBLIOTECARIO, ES_JURADO y
                //  ES_REPRESENTANTE_COMITE a la rama null de arriba (los flags de rol no son ordenables).
            };
            if (UtilObjeto.noEsNulo(ruta)) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private UsuarioSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
