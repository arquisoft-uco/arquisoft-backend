package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Rol. */
public enum RolKey implements ClaveMensaje {

    LOG_ROLES_RECURSO("seguridad.infraestructura.rol.log.roles-recurso");

    private final String clave;

    RolKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.SEGURIDAD;
    }
}
