package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

public enum AlmacenamientoKey implements ClaveMensaje {

    ERROR_URL_CARGA("app.infraestructura.almacenamiento.error.url-carga"),
    ERROR_URL_DESCARGA("app.infraestructura.almacenamiento.error.url-descarga"),
    ERROR_ELIMINACION("app.infraestructura.almacenamiento.error.eliminacion"),
    ERROR_VERIFICACION("app.infraestructura.almacenamiento.error.verificacion"),

    LOG_URL_CARGA_FALLIDA("app.infraestructura.almacenamiento.log.url-carga-fallida"),
    LOG_URL_DESCARGA_FALLIDA("app.infraestructura.almacenamiento.log.url-descarga-fallida"),
    LOG_ELIMINACION_FALLIDA("app.infraestructura.almacenamiento.log.eliminacion-fallida"),
    LOG_BUCKET_CREADO("app.infraestructura.almacenamiento.log.bucket-creado");

    private final String clave;

    AlmacenamientoKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.APP;
    }
}
