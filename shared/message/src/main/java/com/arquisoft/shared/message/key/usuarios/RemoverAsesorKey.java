package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverAsesor. */
public enum RemoverAsesorKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.asesor.error.no-encontrado", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.asesor.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.asesor.log.verificacion-remover", 3),
    LOG_REMOVIDO("usuarios.aplicacion.asesor.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverAsesorKey(String clave, int parametros) {
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
