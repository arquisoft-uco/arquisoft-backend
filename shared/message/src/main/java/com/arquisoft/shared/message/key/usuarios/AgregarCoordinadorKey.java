package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarCoordinador. */
public enum AgregarCoordinadorKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.coordinador.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.coordinador.log.verificacion-agregar", 3),
    LOG_REACTIVADO("usuarios.aplicacion.coordinador.log.reactivado", 1),
    LOG_GUARDADO("usuarios.infraestructura.coordinador.log.guardado", 1),
    LOG_ACTUALIZADO("usuarios.infraestructura.coordinador.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    AgregarCoordinadorKey(String clave, int parametros) {
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
