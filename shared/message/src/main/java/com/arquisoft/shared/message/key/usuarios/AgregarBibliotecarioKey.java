package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AgregarBibliotecario. */
public enum AgregarBibliotecarioKey implements ClaveMensaje {

    ERROR_USUARIO_DUPLICADO("usuarios.dominio.bibliotecario.error.usuario-duplicado", 1),
    LOG_VERIFICACION_AGREGAR("usuarios.aplicacion.bibliotecario.log.verificacion-agregar", 3),
    LOG_REACTIVADO("usuarios.aplicacion.bibliotecario.log.reactivado", 1),
    LOG_GUARDADO("usuarios.infraestructura.bibliotecario.log.guardado", 1),
    LOG_ACTUALIZADO("usuarios.infraestructura.bibliotecario.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    AgregarBibliotecarioKey(String clave, int parametros) {
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
