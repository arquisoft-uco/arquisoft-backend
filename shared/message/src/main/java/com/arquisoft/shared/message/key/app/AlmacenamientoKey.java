package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AlmacenamientoKey implements ClaveMensaje {

    ERROR_URL_CARGA("app.infraestructura.almacenamiento.error.url-carga", 0),
    ERROR_URL_DESCARGA("app.infraestructura.almacenamiento.error.url-descarga", 0),
    ERROR_ELIMINACION("app.infraestructura.almacenamiento.error.eliminacion", 0),
    ERROR_VERIFICACION("app.infraestructura.almacenamiento.error.verificacion", 0),

    LOG_URL_CARGA_FALLIDA("app.infraestructura.almacenamiento.log.url-carga-fallida", 0),
    LOG_URL_DESCARGA_FALLIDA("app.infraestructura.almacenamiento.log.url-descarga-fallida", 0),
    LOG_ELIMINACION_FALLIDA("app.infraestructura.almacenamiento.log.eliminacion-fallida", 0),
    LOG_BUCKET_CREADO("app.infraestructura.almacenamiento.log.bucket-creado", 0);

    private final String clave;
    private final int parametros;

    AlmacenamientoKey(String clave, int parametros) {
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
