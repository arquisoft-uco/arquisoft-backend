package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ResolucionSesionKey implements ClaveMensaje {

    LOG_RESOLVIENDO("seguridad.aplicacion.sesion-externa.log.resolviendo", 0),
    LOG_RESULTADO("seguridad.aplicacion.sesion-externa.log.resultado", 1),
    LOG_IDENTIDAD_CACHE("seguridad.infraestructura.sesion-externa.log.identidad-cache", 1),
    LOG_PROVEEDOR_NO_DISPONIBLE("seguridad.infraestructura.sesion-externa.log.proveedor-no-disponible", 2),
    LOG_CSRF_RECHAZADO("seguridad.infraestructura.sesion-externa.log.csrf-rechazado", 2),
    LOG_SELECCION_RECHAZADA("seguridad.infraestructura.sesion-externa.log.seleccion-rechazada", 1),
    LOG_CONFIGURACION_SESION("seguridad.infraestructura.sesion-externa.log.configuracion", 5),
    ERROR_PROVEEDOR_NO_DISPONIBLE("seguridad.infraestructura.sesion-externa.error.proveedor-no-disponible", 0);

    private final String clave;
    private final int parametros;

    ResolucionSesionKey(String clave, int parametros) {
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
