package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverCoordinador. */
public enum RemoverCoordinadorKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.coordinador.error.no-encontrado", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.coordinador.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.coordinador.log.verificacion-remover", 3),
    LOG_REMOVIDO("usuarios.aplicacion.coordinador.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverCoordinadorKey(String clave, int parametros) {
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
