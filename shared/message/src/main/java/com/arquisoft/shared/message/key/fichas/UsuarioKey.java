package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Usuario. */
public enum UsuarioKey implements ClaveMensaje {

    LOG_USUARIO_CREADO_RECIBIDO("fichas.infraestructura.usuario.log.usuario-creado-recibido", 0),
    LOG_REGISTRADO_ESPEJO_SIMULADO("fichas.infraestructura.usuario.log.registrado-espejo-simulado", 0);

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
