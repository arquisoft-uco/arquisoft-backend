package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EliminarUsuarioKey implements ClaveMensaje {

    ERROR_ELIMINADO("usuarios.dominio.usuario.error.eliminado", 1),
    ERROR_ELIMINADO_DETALLE("usuarios.dominio.usuario.error.eliminado-detalle", 1),
    ERROR_ROLES_VIGENTES("usuarios.dominio.usuario.error.roles-vigentes", 2),
    ERROR_ROLES_VIGENTES_DETALLE("usuarios.dominio.usuario.error.roles-vigentes-detalle", 2),
    LOG_ELIMINANDO("usuarios.aplicacion.usuario.log.eliminando", 1),
    LOG_VERIFICACION_ELIMINAR("usuarios.aplicacion.usuario.log.verificacion-eliminar", 12),
    LOG_ELIMINADO("usuarios.aplicacion.usuario.log.eliminado", 2);

    private final String clave;
    private final int parametros;

    EliminarUsuarioKey(String clave, int parametros) {
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
