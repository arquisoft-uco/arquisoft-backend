package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoFichaPerfil. */
public enum EstadoFichaPerfilKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.estadofichaperfil.error.no-encontrado", 1),
    ERROR_ESTADO_TERMINAL("fichas.dominio.estadofichaperfil.error.estado-terminal", 1),
    LOG_CREADO("fichas.aplicacion.estadofichaperfil.log.creado", 3);

    private final String clave;
    private final int parametros;

    EstadoFichaPerfilKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
