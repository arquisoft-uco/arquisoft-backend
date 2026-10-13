package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RemoverBibliotecario. */
public enum RemoverBibliotecarioKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("usuarios.dominio.bibliotecario.error.no-encontrado", 1),
    LOG_REMOVIENDO("usuarios.aplicacion.bibliotecario.log.removiendo", 1),
    LOG_VERIFICACION_REMOVER("usuarios.aplicacion.bibliotecario.log.verificacion-remover", 3),
    LOG_REMOVIDO("usuarios.aplicacion.bibliotecario.log.removido", 1);

    private final String clave;
    private final int parametros;

    RemoverBibliotecarioKey(String clave, int parametros) {
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
