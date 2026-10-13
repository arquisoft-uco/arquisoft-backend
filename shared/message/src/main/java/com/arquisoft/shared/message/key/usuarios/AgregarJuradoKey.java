package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarJurado. */
public enum AgregarJuradoKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.jurado.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.jurado.log.verificacion-agregar", 3),
    LOG_REACTIVADO("usuarios.aplicacion.jurado.log.reactivado", 1),
    LOG_GUARDADO("usuarios.infraestructura.jurado.log.guardado", 1),
    LOG_ACTUALIZADO("usuarios.infraestructura.jurado.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    AgregarJuradoKey(String clave, int parametros) {
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
