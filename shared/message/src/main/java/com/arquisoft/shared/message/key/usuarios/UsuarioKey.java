package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Usuario. */
public enum UsuarioKey implements ClaveMensaje {

    ERROR_EMAIL_DUPLICADO("usuarios.dominio.usuario.error.email-duplicado"),
    ERROR_ROL_NO_ENCONTRADO("usuarios.dominio.usuario.error.rol-no-encontrado"),
    ERROR_EMAIL_REQUERIDO("usuarios.dominio.usuario.error.email-requerido"),
    ERROR_ROL_REQUERIDO("usuarios.dominio.usuario.error.rol-requerido"),
    LOG_CREADO("usuarios.aplicacion.usuario.log.creado"),
    LOG_MOCK_NO_PERSISTIDO("usuarios.infraestructura.usuario.log.mock-no-persistido"),
    LOG_MOCK_VERIFICACION_OMITIDA("usuarios.infraestructura.usuario.log.mock-verificacion-omitida");

    private final String clave;

    UsuarioKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.USUARIOS;
    }
}
