package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

public enum ConfiguracionKey implements ClaveMensaje {

    LOG_REST_TEMPLATE_CONFIGURADO("seguridad.infraestructura.configuracion.log.rest-template-configurado"),
    LOG_HTTP_PETICION("seguridad.infraestructura.configuracion.log.http-peticion"),
    LOG_HTTP_RESPUESTA("seguridad.infraestructura.configuracion.log.http-respuesta"),
    LOG_CORS_CONFIGURADO("seguridad.infraestructura.configuracion.log.cors-configurado");

    private final String clave;

    ConfiguracionKey(String clave) {
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
