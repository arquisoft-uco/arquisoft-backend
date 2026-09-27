package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverRepresentanteComite. */
public enum RemoverRepresentanteComiteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.representantecomite.error.no-encontrado", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.representantecomite.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.representantecomite.log.verificacion-remover", 3),
    LOG_REMOVIDO("usuarios.aplicacion.representantecomite.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverRepresentanteComiteKey(String clave, int parametros) {
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
