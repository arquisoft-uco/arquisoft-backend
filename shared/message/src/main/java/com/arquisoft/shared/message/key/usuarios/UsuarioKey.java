package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Usuario. */
public enum UsuarioKey implements ClaveMensaje {

    ERROR_EMAIL_DUPLICADO("usuarios.dominio.usuario.error.email-duplicado", 1),
    ERROR_ROL_NO_ENCONTRADO("usuarios.dominio.usuario.error.rol-no-encontrado", 1),
    LOG_CREADO("usuarios.aplicacion.usuario.log.creado", 3);

    private final String clave;
    private final int parametros;

    UsuarioKey(String clave, int parametros) {
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
