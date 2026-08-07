package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Usuario. */
public enum UsuarioKey implements ClaveMensaje {

    ERROR_EMAIL_DUPLICADO("usuarios.dominio.usuario.error.email-duplicado"),
    LOG_CREADO("usuarios.aplicacion.usuario.log.creado");

    private final String clave;

    UsuarioKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.USUARIOS;
    }
}
