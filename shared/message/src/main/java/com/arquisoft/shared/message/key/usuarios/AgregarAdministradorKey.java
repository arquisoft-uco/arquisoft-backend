package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarAdministrador. */
public enum AgregarAdministradorKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.administrador.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.administrador.log.verificacion-agregar", 3),
    LOG_REACTIVADO("usuarios.aplicacion.administrador.log.reactivado", 1),
    LOG_GUARDADO("usuarios.infraestructura.administrador.log.guardado", 1),
    LOG_ACTUALIZADO("usuarios.infraestructura.administrador.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    AgregarAdministradorKey(String clave, int parametros) {
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
