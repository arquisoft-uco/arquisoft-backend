package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverAsesorFicha. */
public enum RemoverAsesorFichaKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.asesorficha.error.no-encontrado", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.asesorficha.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.asesorficha.log.verificacion-remover", 3),
    LOG_REMOVIDO("usuarios.aplicacion.asesorficha.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverAsesorFichaKey(String clave, int parametros) {
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
