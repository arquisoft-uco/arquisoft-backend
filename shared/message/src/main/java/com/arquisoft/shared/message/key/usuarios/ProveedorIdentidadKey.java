package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves del proveedor de identidad para realm roles. */
public enum ProveedorIdentidadKey implements ClaveMensaje {

    ERROR_NO_DISPONIBLE("usuarios.infraestructura.proveedor-identidad.error.no-disponible", 0),
    LOG_ROL_REVOCADO("usuarios.infraestructura.proveedor-identidad.log.rol-revocado", 2),
    LOG_COMPENSACION_ROL_FALLIDA("usuarios.infraestructura.proveedor-identidad.log.compensacion-rol-fallida", 2),
    LOG_IDENTIDAD_ACTUALIZADA("usuarios.infraestructura.proveedor-identidad.log.identidad-actualizada", 1),
    LOG_COMPENSACION_IDENTIDAD_FALLIDA(
            "usuarios.infraestructura.proveedor-identidad.log.compensacion-identidad-fallida", 1);

    private final String clave;
    private final int parametros;

    ProveedorIdentidadKey(String clave, int parametros) {
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
