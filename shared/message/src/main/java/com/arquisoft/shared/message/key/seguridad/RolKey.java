package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Rol. */
public enum RolKey implements ClaveMensaje {

    LOG_ROLES_RECURSO("seguridad.infraestructura.rol.log.roles-recurso", 1);

    private final String clave;
    private final int parametros;

    RolKey(String clave, int parametros) {
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
