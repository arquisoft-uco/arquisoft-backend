package com.arquisoft.shared.message.key.seguridad;

import com.arquisoft.shared.message.ClaveMensaje;

public enum MapeoRolesKey implements ClaveMensaje {

    LOG_VERIFICACION_VIGENCIA("seguridad.aplicacion.mapeoroles.log.verificacion-vigencia", 1),
    LOG_ACTUALIZACION_OMITIDA("seguridad.aplicacion.mapeoroles.log.actualizacion-omitida", 0),
    LOG_ACTUALIZANDO("seguridad.aplicacion.mapeoroles.log.actualizando", 0),
    LOG_ACTUALIZADA("seguridad.aplicacion.mapeoroles.log.actualizada", 2),
    LOG_MAPEO_CONSERVADO("seguridad.aplicacion.mapeoroles.log.mapeo-conservado", 1),
    LOG_MAPEO_GUARDADO("seguridad.infraestructura.mapeoroles.log.mapeo-guardado", 2),
    LOG_ALMACEN_NO_DISPONIBLE("seguridad.infraestructura.mapeoroles.log.almacen-no-disponible", 1),
    LOG_SIN_MAPEO("seguridad.infraestructura.mapeoroles.log.sin-mapeo", 0),
    LOG_FUENTE_NO_DISPONIBLE("seguridad.infraestructura.mapeoroles.log.fuente-no-disponible", 2),
    LOG_REFRESCO_INESPERADO("seguridad.infraestructura.mapeoroles.log.refresco-inesperado", 1);

    private final String clave;
    private final int parametros;

    MapeoRolesKey(String clave, int parametros) {
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
