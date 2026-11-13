package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum PaqueteEntregableKey implements ClaveMensaje {

    LOG_PAQUETE_RECIBIDO("entregables.infraestructura.paqueteentregable.log.paquete-recibido", 3),
    LOG_VERIFICACION_AGREGAR("entregables.aplicacion.paqueteentregable.log.verificacion-agregar", 2),
    LOG_AGREGADO("entregables.aplicacion.paqueteentregable.log.agregado", 1),
    LOG_DUPLICADO("entregables.aplicacion.paqueteentregable.log.duplicado", 1),
    LOG_GUARDADO("entregables.infraestructura.paqueteentregable.log.guardado", 1),
    LOG_DESCARGANDO("entregables.aplicacion.paqueteentregable.log.descargando", 2),
    LOG_VERIFICACION_DESCARGA("entregables.aplicacion.paqueteentregable.log.verificacion-descarga", 4),
    LOG_DESCARGA_GENERADA("entregables.aplicacion.paqueteentregable.log.descarga-generada", 2),
    ERROR_ACCESO_DENEGADO("entregables.dominio.paqueteentregable.error.acceso-denegado", 0),
    ERROR_ACCESO_DENEGADO_DETALLE("entregables.dominio.paqueteentregable.error.acceso-denegado-detalle", 2),
    ERROR_EN_PREPARACION("entregables.dominio.paqueteentregable.error.en-preparacion", 0),
    ERROR_EN_PREPARACION_DETALLE("entregables.dominio.paqueteentregable.error.en-preparacion-detalle", 1),
    ERROR_CANCELADO("entregables.dominio.paqueteentregable.error.cancelado", 1),
    ERROR_CANCELADO_DETALLE("entregables.dominio.paqueteentregable.error.cancelado-detalle", 1),
    ERROR_PROYECTO_CANCELADO("entregables.dominio.paqueteentregable.error.proyecto-cancelado", 0),
    ERROR_PROYECTO_CANCELADO_DETALLE("entregables.dominio.paqueteentregable.error.proyecto-cancelado-detalle", 1);

    private final String clave;
    private final int parametros;

    PaqueteEntregableKey(String clave, int parametros) {
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
