package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConfiguracionKey implements ClaveMensaje {

    LOG_REST_TEMPLATE_CONFIGURADO("seguridad.infraestructura.configuracion.log.rest-template-configurado", 0),
    LOG_HTTP_PETICION("seguridad.infraestructura.configuracion.log.http-peticion", 0),
    LOG_HTTP_RESPUESTA("seguridad.infraestructura.configuracion.log.http-respuesta", 0),
    LOG_CORS_CONFIGURADO("seguridad.infraestructura.configuracion.log.cors-configurado", 0);

    private final String clave;
    private final int parametros;

    ConfiguracionKey(String clave, int parametros) {
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
