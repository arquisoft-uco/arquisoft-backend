package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.keycloak.mapper;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;

import java.util.Map;

public final class KeycloakIdentidadUsuarioMapper {

    private static final String CAMPO_FIRST_NAME = "firstName";
    private static final String CAMPO_LAST_NAME = "lastName";

    private KeycloakIdentidadUsuarioMapper() {}

    public static IdentidadUsuarioReadModel toReadModel(Map<String, Object> usuario) {
        return new IdentidadUsuarioReadModel(
                texto(usuario.get(CAMPO_FIRST_NAME)),
                texto(usuario.get(CAMPO_LAST_NAME)));
    }

    private static String texto(Object valor) {
        return UtilObjeto.esNulo(valor) ? UtilTexto.VACIO : String.valueOf(valor);
    }
}
