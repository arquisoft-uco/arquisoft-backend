package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarAsesor. */
public enum AgregarAsesorKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.asesor.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.asesor.log.verificacion-agregar", 3),
    LOG_REACTIVADO("usuarios.aplicacion.asesor.log.reactivado", 1),
    LOG_GUARDADO("usuarios.infraestructura.asesor.log.guardado", 1),
    LOG_ACTUALIZADO("usuarios.infraestructura.asesor.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    AgregarAsesorKey(String clave, int parametros) {
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
