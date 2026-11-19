package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CatalogoParametrosKey implements ClaveMensaje {

    LOG_REFRESCANDO("app.aplicacion.catalogoparametros.log.refrescando", 0),
    LOG_REFRESCADO("app.aplicacion.catalogoparametros.log.refrescado", 1),
    LOG_CONSERVADO("app.aplicacion.catalogoparametros.log.conservado", 1),
    LOG_RECHAZADO("app.aplicacion.catalogoparametros.log.rechazado", 1),
    LOG_SUSPENDIDO("app.aplicacion.catalogoparametros.log.suspendido", 0),

    LOG_REFRESCO_INESPERADO("app.infraestructura.catalogoparametros.log.refresco-inesperado", 1),
    LOG_CARGA_INICIAL_FALLIDA("app.infraestructura.catalogoparametros.log.carga-inicial-fallida", 1),
    LOG_CARGA_INICIAL("app.infraestructura.catalogoparametros.log.carga-inicial", 1),
    LOG_CONFIGURACION("app.infraestructura.catalogoparametros.log.configuracion", 3),

    ERROR_NO_DISPONIBLE("app.infraestructura.catalogoparametros.error.no-disponible", 0),
    ERROR_NO_DISPONIBLE_DETALLE("app.infraestructura.catalogoparametros.error.no-disponible-detalle", 1),
    ERROR_CONFIGURACION_INVALIDA("app.infraestructura.catalogoparametros.error.configuracion-invalida", 1),
    ERROR_CARGA_INICIAL_FALLIDA("app.infraestructura.catalogoparametros.error.carga-inicial-fallida", 0),
    ERROR_CARGA_INICIAL_FALLIDA_DETALLE("app.infraestructura.catalogoparametros.error.carga-inicial-fallida-detalle", 1);

    private final String clave;
    private final int parametros;

    CatalogoParametrosKey(String clave, int parametros) {
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
