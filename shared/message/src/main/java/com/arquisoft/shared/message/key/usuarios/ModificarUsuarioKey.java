package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ModificarUsuarioKey implements ClaveMensaje {

    ERROR_MODIFICACION_VACIA("usuarios.aplicacion.usuario.error.modificacion-vacia", 0),
    ERROR_NO_ENCONTRADO("usuarios.dominio.usuario.error.no-encontrado", 1),
    ERROR_INACTIVO("usuarios.dominio.usuario.error.inactivo", 1),
    LOG_MODIFICANDO("usuarios.aplicacion.usuario.log.modificando", 1),
    LOG_VERIFICACION_MODIFICAR("usuarios.aplicacion.usuario.log.verificacion-modificar", 6),
    LOG_MODIFICADO("usuarios.aplicacion.usuario.log.modificado", 2),
    LOG_ACTUALIZADO("usuarios.infraestructura.usuario.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    ModificarUsuarioKey(String clave, int parametros) {
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
