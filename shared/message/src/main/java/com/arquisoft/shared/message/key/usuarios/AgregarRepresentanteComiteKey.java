package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarRepresentanteComite. */
public enum AgregarRepresentanteComiteKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.representantecomite.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.representantecomite.log.verificacion-agregar", 3),
    LOG_REACTIVADO("usuarios.aplicacion.representantecomite.log.reactivado", 1),
    LOG_GUARDADO("usuarios.infraestructura.representantecomite.log.guardado", 1),
    LOG_ACTUALIZADO("usuarios.infraestructura.representantecomite.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    AgregarRepresentanteComiteKey(String clave, int parametros) {
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
