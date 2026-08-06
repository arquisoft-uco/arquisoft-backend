package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de Usuario. */
public enum UsuarioKey implements MessageKey {

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
    public String bundle() {
        return MessageBundles.USUARIOS;
    }
}
