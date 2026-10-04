package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum MapaRutaKey implements ClaveMensaje {

    ERROR_DUPLICADO("mapas_ruta.dominio.maparuta.error.duplicado", 1),
    LOG_AGREGANDO("mapas_ruta.aplicacion.maparuta.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("mapas_ruta.aplicacion.maparuta.log.verificacion-agregar", 2),
    LOG_AGREGADO("mapas_ruta.aplicacion.maparuta.log.agregado", 2),
    LOG_GUARDADO("mapas_ruta.infraestructura.maparuta.log.guardado", 1),
    LOG_CONSULTANDO_ESTUDIANTE("mapas_ruta.aplicacion.maparuta.log.consultando-estudiante", 1),
    LOG_CONSULTA_ESTUDIANTE_COMPLETADA("mapas_ruta.aplicacion.maparuta.log.consulta-estudiante-completada", 2);

    private final String clave;
    private final int parametros;

    MapaRutaKey(String clave, int parametros) {
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
