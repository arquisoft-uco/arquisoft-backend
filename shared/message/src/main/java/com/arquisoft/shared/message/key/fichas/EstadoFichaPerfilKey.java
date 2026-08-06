package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de EstadoFichaPerfil. */
public enum EstadoFichaPerfilKey implements MessageKey {

    ERROR_NO_ENCONTRADO("fichas.dominio.estadofichaperfil.error.no-encontrado"),
    ERROR_ESTADO_TERMINAL("fichas.dominio.estadofichaperfil.error.estado-terminal"),
    LOG_CREADO("fichas.aplicacion.estadofichaperfil.log.creado");

    private final String clave;

    EstadoFichaPerfilKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String bundle() {
        return MessageBundles.FICHAS;
    }
}
