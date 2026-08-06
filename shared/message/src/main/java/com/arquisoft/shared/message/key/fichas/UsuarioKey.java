package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.MessageBundles;
import com.arquisoft.shared.message.MessageKey;

/** Claves de Usuario. */
public enum UsuarioKey implements MessageKey {

    LOG_USUARIO_CREADO_RECIBIDO("fichas.infraestructura.usuario.log.usuario-creado-recibido"),
    LOG_REGISTRADO_ESPEJO_SIMULADO("fichas.infraestructura.usuario.log.registrado-espejo-simulado");

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
        return MessageBundles.FICHAS;
    }
}
