package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoFichaPerfil. */
public enum EstadoFichaPerfilKey implements ClaveMensaje {

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
    public String paquete() {
        return PaquetesMensajes.FICHAS;
    }
}
