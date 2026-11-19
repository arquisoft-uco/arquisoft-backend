package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RevisionItem. */
public enum RevisionItemKey implements ClaveMensaje {

    ERROR_ESTADO_NO_ENCONTRADO("fichas.dominio.revisionitem.error.estado-no-encontrado", 0),
    ERROR_ESTADO_NO_ENCONTRADO_DETALLE("fichas.dominio.revisionitem.error.estado-no-encontrado-detalle", 1),
    ERROR_YA_EXISTE("fichas.dominio.revisionitem.error.ya-existe", 0),
    ERROR_YA_EXISTE_DETALLE("fichas.dominio.revisionitem.error.ya-existe-detalle", 1),
    ERROR_NO_ENCONTRADA("fichas.dominio.revisionitem.error.no-encontrada", 0),
    ERROR_NO_ENCONTRADA_DETALLE("fichas.dominio.revisionitem.error.no-encontrada-detalle", 1),
    ERROR_CERRADA("fichas.dominio.revisionitem.error.cerrada", 0),
    ERROR_CERRADA_DETALLE("fichas.dominio.revisionitem.error.cerrada-detalle", 1),
    ERROR_NO_EN_PROGRESO("fichas.dominio.revisionitem.error.no-en-progreso", 0),
    ERROR_NO_EN_PROGRESO_DETALLE("fichas.dominio.revisionitem.error.no-en-progreso-detalle", 1),
    ERROR_NO_CORRECCION_DISPONIBLE("fichas.dominio.revisionitem.error.no-correccion-disponible", 0),
    ERROR_NO_CORRECCION_DISPONIBLE_DETALLE("fichas.dominio.revisionitem.error.no-correccion-disponible-detalle", 1),
    ERROR_SIN_OBSERVACIONES("fichas.dominio.revisionitem.error.sin-observaciones", 0),
    ERROR_SIN_OBSERVACIONES_DETALLE("fichas.dominio.revisionitem.error.sin-observaciones-detalle", 1),
    ERROR_OBSERVACIONES_PENDIENTES("fichas.dominio.revisionitem.error.observaciones-pendientes", 0),
    ERROR_OBSERVACIONES_PENDIENTES_DETALLE("fichas.dominio.revisionitem.error.observaciones-pendientes-detalle", 2),
    LOG_AGREGANDO("fichas.aplicacion.revisionitem.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.revisionitem.log.verificacion-agregar", 3),
    LOG_AGREGADO("fichas.aplicacion.revisionitem.log.agregado", 2),
    LOG_CONSULTANDO_ELABORADAS("fichas.aplicacion.revisionitem.log.consultando-elaboradas", 2),
    LOG_CONSULTA_ELABORADAS_COMPLETADA("fichas.aplicacion.revisionitem.log.consulta-elaboradas-completada", 1),
    LOG_CONSULTANDO_ESTUDIANTE("fichas.aplicacion.revisionitem.log.consultando-estudiante", 2),
    LOG_CONSULTA_ESTUDIANTE_COMPLETADA("fichas.aplicacion.revisionitem.log.consulta-estudiante-completada", 1),
    LOG_VISUALIZANDO("fichas.aplicacion.revisionitem.log.visualizando", 2),
    LOG_VERIFICACION_VISUALIZAR("fichas.aplicacion.revisionitem.log.verificacion-visualizar", 3),
    LOG_YA_VISUALIZADA("fichas.aplicacion.revisionitem.log.ya-visualizada", 2),
    LOG_VISUALIZADA("fichas.aplicacion.revisionitem.log.visualizada", 1),
    LOG_REMOVIENDO("fichas.aplicacion.revisionitem.log.removiendo", 2),
    LOG_VERIFICACION_REMOVER("fichas.aplicacion.revisionitem.log.verificacion-remover", 3),
    LOG_REMOVIDA("fichas.aplicacion.revisionitem.log.removida", 1),
    LOG_ENVIANDO_CORRECCIONES("fichas.aplicacion.revisionitem.log.enviando-correcciones", 2),
    LOG_VERIFICACION_ENVIAR_CORRECCIONES("fichas.aplicacion.revisionitem.log.verificacion-enviar-correcciones", 5),
    LOG_CORRECCIONES_ENVIADAS("fichas.aplicacion.revisionitem.log.correcciones-enviadas", 1),
    LOG_ACTUALIZADO("fichas.infraestructura.revisionitem.log.actualizado", 2),
    LOG_ELIMINADO("fichas.infraestructura.revisionitem.log.eliminado", 1);

    private final String clave;
    private final int parametros;

    RevisionItemKey(String clave, int parametros) {
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
