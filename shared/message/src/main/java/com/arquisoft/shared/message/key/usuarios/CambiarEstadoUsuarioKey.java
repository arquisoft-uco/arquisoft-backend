package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CambiarEstadoUsuarioKey implements ClaveMensaje {

    ERROR_ESTADO_INVALIDO("usuarios.dominio.usuario.error.estado-invalido", 1),
    ERROR_ESTADO_SIN_CAMBIO("usuarios.dominio.usuario.error.estado-sin-cambio", 2),
    LOG_CAMBIANDO_ESTADO("usuarios.aplicacion.usuario.log.cambiando-estado", 2),
    LOG_VERIFICACION_CAMBIAR_ESTADO("usuarios.aplicacion.usuario.log.verificacion-cambiar-estado", 4),
    LOG_ESTADO_CAMBIADO("usuarios.aplicacion.usuario.log.estado-cambiado", 3);

    private final String clave;
    private final int parametros;

    CambiarEstadoUsuarioKey(String clave, int parametros) {
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
