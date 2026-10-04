package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverAdministrador. */
public enum RemoverAdministradorKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.administrador.error.no-encontrado", 1),
    ERROR_AUTOELIMINACION("usuarios.dominio.administrador.error.autoeliminacion", 1),
    ERROR_UNICO_VIGENTE("usuarios.dominio.administrador.error.unico-vigente", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.administrador.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.administrador.log.verificacion-remover", 4),
    LOG_REMOVIDO("usuarios.aplicacion.administrador.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverAdministradorKey(String clave, int parametros) {
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
