package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum DocumentacionKey implements ClaveMensaje {

    LOG_CONSULTANDO("biblioteca.aplicacion.documentacion.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("biblioteca.aplicacion.documentacion.log.consulta-completada", 3),
    LOG_DESCARGANDO("biblioteca.aplicacion.documentacion.log.descargando", 2),
    LOG_VERIFICACION_DESCARGA("biblioteca.aplicacion.documentacion.log.verificacion-descarga", 2),
    LOG_DESCARGA_GENERADA("biblioteca.aplicacion.documentacion.log.descarga-generada", 2),
    ERROR_NO_DESCARGABLE("biblioteca.dominio.documentacion.error.no-descargable", 0),
    ERROR_NO_DESCARGABLE_DETALLE("biblioteca.dominio.documentacion.error.no-descargable-detalle", 1),
    ERROR_ARCHIVO_NO_DISPONIBLE("biblioteca.dominio.documentacion.error.archivo-no-disponible", 0),
    ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE("biblioteca.dominio.documentacion.error.archivo-no-disponible-detalle", 1),
    ERROR_ARCHIVO_AUSENTE("biblioteca.infraestructura.documentacion.error.archivo-ausente", 0),
    ERROR_ARCHIVO_AUSENTE_DETALLE("biblioteca.infraestructura.documentacion.error.archivo-ausente-detalle", 3),
    LOG_CAMBIANDO_ESTADO("biblioteca.aplicacion.documentacion.log.cambiando-estado", 2),
    LOG_VERIFICACION_CAMBIAR_ESTADO("biblioteca.aplicacion.documentacion.log.verificacion-cambiar-estado", 2),
    LOG_ESTADO_CAMBIADO("biblioteca.aplicacion.documentacion.log.estado-cambiado", 3),
    LOG_ESTADO_ACTUALIZADO("biblioteca.infraestructura.documentacion.log.estado-actualizado", 1),
    ERROR_NO_ENCONTRADA("biblioteca.dominio.documentacion.error.no-encontrada", 0),
    ERROR_NO_ENCONTRADA_DETALLE("biblioteca.dominio.documentacion.error.no-encontrada-detalle", 1),
    ERROR_ESTADO_INVALIDO("biblioteca.dominio.documentacion.error.estado-invalido", 0),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO("biblioteca.dominio.documentacion.error.estado-destino-no-permitido", 1),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE(
            "biblioteca.dominio.documentacion.error.estado-destino-no-permitido-detalle", 2),
    ERROR_ESTADO_SIN_CAMBIO("biblioteca.dominio.documentacion.error.estado-sin-cambio", 1),
    ERROR_ESTADO_SIN_CAMBIO_DETALLE("biblioteca.dominio.documentacion.error.estado-sin-cambio-detalle", 2),
    LOG_REGISTRANDO("biblioteca.aplicacion.documentacion.log.registrando", 2),
    LOG_VERIFICACION_REGISTRO("biblioteca.aplicacion.documentacion.log.verificacion-registro", 4),
    LOG_REGISTRADA("biblioteca.aplicacion.documentacion.log.registrada", 3),
    LOG_GUARDADA("biblioteca.infraestructura.documentacion.log.guardada", 1),
    ERROR_YA_REGISTRADA("biblioteca.dominio.documentacion.error.ya-registrada", 0),
    ERROR_YA_REGISTRADA_DETALLE("biblioteca.dominio.documentacion.error.ya-registrada-detalle", 1),
    LOG_CONSULTANDO_ENLACE("biblioteca.aplicacion.documentacion.log.consultando-enlace", 1),
    LOG_VERIFICACION_ENLACE("biblioteca.aplicacion.documentacion.log.verificacion-enlace", 2),
    LOG_ENLACE_GENERADO("biblioteca.aplicacion.documentacion.log.enlace-generado", 2),
    ERROR_NO_COMPARTIBLE("biblioteca.dominio.documentacion.error.no-compartible", 0),
    ERROR_NO_COMPARTIBLE_DETALLE("biblioteca.dominio.documentacion.error.no-compartible-detalle", 1);

    private final String clave;
    private final int parametros;

    DocumentacionKey(String clave, int parametros) {
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
