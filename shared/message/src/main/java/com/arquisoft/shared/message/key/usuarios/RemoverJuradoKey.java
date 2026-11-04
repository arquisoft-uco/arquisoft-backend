package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverJurado. */
public enum RemoverJuradoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.jurado.error.no-encontrado", 1),
    ERROR_NO_ENCONTRADO_DETALLE("usuarios.dominio.jurado.error.no-encontrado-detalle", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.jurado.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.jurado.log.verificacion-remover", 4),
    LOG_REMOVIDO("usuarios.aplicacion.jurado.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverJuradoKey(String clave, int parametros) {
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
