package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarEstudiante. */
public enum AgregarEstudianteKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.estudiante.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.estudiante.log.verificacion-agregar", 3),
    LOG_REACTIVADO("usuarios.aplicacion.estudiante.log.reactivado", 1),
    LOG_GUARDADO("usuarios.infraestructura.estudiante.log.guardado", 1),
    LOG_ACTUALIZADO("usuarios.infraestructura.estudiante.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    AgregarEstudianteKey(String clave, int parametros) {
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
