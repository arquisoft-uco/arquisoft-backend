package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverEstudiante. */
public enum RemoverEstudianteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.estudiante.error.no-encontrado", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.estudiante.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.estudiante.log.verificacion-remover", 3),
    LOG_REMOVIDO("usuarios.aplicacion.estudiante.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverEstudianteKey(String clave, int parametros) {
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
