package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Usuario. */
public enum UsuarioKey implements ClaveMensaje {

    ERROR_EMAIL_DUPLICADO("usuarios.dominio.usuario.error.email-duplicado", 1),
    ERROR_ROL_NO_ENCONTRADO("usuarios.dominio.usuario.error.rol-no-encontrado", 1),
    ERROR_EMAIL_REQUERIDO("usuarios.dominio.usuario.error.email-requerido", 0),
    ERROR_ROL_REQUERIDO("usuarios.dominio.usuario.error.rol-requerido", 0),
    LOG_CREADO("usuarios.aplicacion.usuario.log.creado", 3),
    LOG_MOCK_NO_PERSISTIDO("usuarios.infraestructura.usuario.log.mock-no-persistido", 2),
    LOG_MOCK_VERIFICACION_OMITIDA("usuarios.infraestructura.usuario.log.mock-verificacion-omitida", 1);

    private final String clave;
    private final int parametros;

    UsuarioKey(String clave, int parametros) {
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
