package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum PaqueteEntregableKey implements ClaveMensaje {

    LOG_GUARDADO("entregables.infraestructura.paqueteentregable.log.guardado", 1),
    LOG_PAQUETE_ARMADO("entregables.infraestructura.paqueteentregable.log.paquete-armado", 3),
    LOG_PAQUETE_ELIMINADO("entregables.infraestructura.paqueteentregable.log.paquete-eliminado", 2),
    LOG_PURGADOS("entregables.infraestructura.paqueteentregable.log.purgados", 2),
    LOG_DESCARGANDO("entregables.aplicacion.paqueteentregable.log.descargando", 2),
    LOG_VERIFICACION_DESCARGA("entregables.aplicacion.paqueteentregable.log.verificacion-descarga", 5),
    LOG_DESCARGA_GENERADA("entregables.aplicacion.paqueteentregable.log.descarga-generada", 2),
    ERROR_ACCESO_DENEGADO("entregables.dominio.paqueteentregable.error.acceso-denegado", 0),
    ERROR_ACCESO_DENEGADO_DETALLE("entregables.dominio.paqueteentregable.error.acceso-denegado-detalle", 2),
    ERROR_NO_DISPONIBLE("entregables.dominio.paqueteentregable.error.no-disponible", 0),
    ERROR_NO_DISPONIBLE_DETALLE("entregables.dominio.paqueteentregable.error.no-disponible-detalle", 1),
    ERROR_ARCHIVO_ARTEFACTO_NO_DISPONIBLE(
            "entregables.infraestructura.paqueteentregable.error.archivo-artefacto-no-disponible", 1),
    ERROR_ARCHIVO_ARTEFACTO_NO_DISPONIBLE_DETALLE(
            "entregables.infraestructura.paqueteentregable.error.archivo-artefacto-no-disponible-detalle", 2),
    ERROR_CANCELADO("entregables.dominio.paqueteentregable.error.cancelado", 1),
    ERROR_CANCELADO_DETALLE("entregables.dominio.paqueteentregable.error.cancelado-detalle", 1),
    ERROR_PROYECTO_CANCELADO("entregables.dominio.paqueteentregable.error.proyecto-cancelado", 0),
    ERROR_PROYECTO_CANCELADO_DETALLE("entregables.dominio.paqueteentregable.error.proyecto-cancelado-detalle", 1),
    ERROR_DESACTUALIZADO("entregables.dominio.paqueteentregable.error.desactualizado", 1),
    ERROR_DESACTUALIZADO_DETALLE("entregables.dominio.paqueteentregable.error.desactualizado-detalle", 5),
    LOG_EFECTO_DIFERIDO_FALLIDO("entregables.infraestructura.paqueteentregable.log.efecto-diferido-fallido", 2);

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
