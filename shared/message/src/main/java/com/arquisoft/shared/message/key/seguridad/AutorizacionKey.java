package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Autorización. */
public enum AutorizacionKey implements ClaveMensaje {

    ERROR_PROVEEDOR_NO_DISPONIBLE("seguridad.infraestructura.autorizacion.error.proveedor-no-disponible", 0),
    ERROR_CONFIGURACION_INSEGURA("seguridad.infraestructura.autorizacion.error.configuracion-insegura", 0),
    LOG_AUTORIZANDO("seguridad.aplicacion.autorizacion.log.autorizando", 2),
    LOG_DECISION("seguridad.aplicacion.autorizacion.log.decision", 3),
    LOG_PEP_NO_DISPONIBLE("seguridad.infraestructura.autorizacion.log.pep-no-disponible", 2),
    LOG_CONFIGURACION_AUTORIZACION("seguridad.infraestructura.autorizacion.log.configuracion", 3),
    LOG_AUDIENCIA_SIN_VALIDAR("seguridad.infraestructura.autorizacion.log.audiencia-sin-validar", 1);

    private final String clave;
    private final int parametros;

    AutorizacionKey(String clave, int parametros) {
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
