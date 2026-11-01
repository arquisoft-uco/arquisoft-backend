package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RevisionAsesor. */
public enum RevisionAsesorKey implements ClaveMensaje {

    LOG_CONSULTANDO_ESTUDIANTE("artefactos.aplicacion.revisionasesor.log.consultando-estudiante", 2),
    LOG_CONSULTA_ESTUDIANTE_COMPLETADA("artefactos.aplicacion.revisionasesor.log.consulta-estudiante-completada", 1),
    LOG_CONSULTANDO_ASESOR("artefactos.aplicacion.revisionasesor.log.consultando-asesor", 4),
    LOG_CONSULTA_ASESOR_COMPLETADA("artefactos.aplicacion.revisionasesor.log.consulta-asesor-completada", 1),
    ERROR_NO_ENCONTRADA("artefactos.dominio.revisionasesor.error.no-encontrada", 0),
    ERROR_NO_ENCONTRADA_DETALLE("artefactos.dominio.revisionasesor.error.no-encontrada-detalle", 1),
    ERROR_CERRADA("artefactos.dominio.revisionasesor.error.cerrada", 0),
    ERROR_CERRADA_DETALLE("artefactos.dominio.revisionasesor.error.cerrada-detalle", 1),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO("artefactos.dominio.revisionasesor.error.estado-destino-no-permitido", 1),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE(
            "artefactos.dominio.revisionasesor.error.estado-destino-no-permitido-detalle", 2),
    ERROR_ESTADO_INVALIDO("artefactos.dominio.revisionasesor.error.estado-invalido", 1),
    ERROR_ESTUDIANTE_NO_PARTICIPA("artefactos.dominio.revisionasesor.error.estudiante-no-participa", 0),
    ERROR_ESTUDIANTE_NO_PARTICIPA_DETALLE(
            "artefactos.dominio.revisionasesor.error.estudiante-no-participa-detalle", 2),
    ERROR_ABIERTA_EXISTENTE("artefactos.dominio.revisionasesor.error.abierta-existente", 1),
    ERROR_ABIERTA_EXISTENTE_DETALLE("artefactos.dominio.revisionasesor.error.abierta-existente-detalle", 1),
    ERROR_VERSION_ANTERIOR_SIN_REVISION(
            "artefactos.dominio.revisionasesor.error.version-anterior-sin-revision", 1),
    ERROR_VERSION_ANTERIOR_SIN_REVISION_DETALLE(
            "artefactos.dominio.revisionasesor.error.version-anterior-sin-revision-detalle", 1),
    ERROR_ARTEFACTO_CERRADO("artefactos.dominio.revisionasesor.error.artefacto-cerrado", 0),
    ERROR_ARTEFACTO_CERRADO_DETALLE("artefactos.dominio.revisionasesor.error.artefacto-cerrado-detalle", 1),
    ERROR_VERSION_ANTERIOR_NO_EN_PROGRESO(
            "artefactos.dominio.revisionasesor.error.version-anterior-no-en-progreso", 2),
    ERROR_VERSION_ANTERIOR_NO_EN_PROGRESO_DETALLE(
            "artefactos.dominio.revisionasesor.error.version-anterior-no-en-progreso-detalle", 2),
    ERROR_PENDIENTE("artefactos.dominio.revisionasesor.error.pendiente", 0),
    ERROR_PENDIENTE_DETALLE("artefactos.dominio.revisionasesor.error.pendiente-detalle", 1),
    ERROR_SIN_VERSION_POSTERIOR("artefactos.dominio.revisionasesor.error.sin-version-posterior", 0),
    ERROR_SIN_VERSION_POSTERIOR_DETALLE("artefactos.dominio.revisionasesor.error.sin-version-posterior-detalle", 1),
    ERROR_NO_ADMITE_OBSERVACIONES("artefactos.dominio.revisionasesor.error.no-admite-observaciones", 0),
    ERROR_NO_ADMITE_OBSERVACIONES_DETALLE(
            "artefactos.dominio.revisionasesor.error.no-admite-observaciones-detalle", 2),
    ERROR_NO_ADMITE_ELIMINAR_OBSERVACIONES(
            "artefactos.dominio.revisionasesor.error.no-admite-eliminar-observaciones", 0),
    ERROR_NO_ADMITE_ELIMINAR_OBSERVACIONES_DETALLE(
            "artefactos.dominio.revisionasesor.error.no-admite-eliminar-observaciones-detalle", 2),
    ERROR_NO_ENVIABLE("artefactos.dominio.revisionasesor.error.no-enviable", 0),
    ERROR_NO_ENVIABLE_DETALLE("artefactos.dominio.revisionasesor.error.no-enviable-detalle", 2),
    ERROR_NO_ELIMINABLE("artefactos.dominio.revisionasesor.error.no-eliminable", 0),
    ERROR_NO_ELIMINABLE_DETALLE("artefactos.dominio.revisionasesor.error.no-eliminable-detalle", 2),
    LOG_ELIMINADA("artefactos.infraestructura.revisionasesor.log.eliminada", 1),
    ERROR_NO_ADMITE_DECISION_OBSERVACION(
            "artefactos.dominio.revisionasesor.error.no-admite-decision-observacion", 0),
    ERROR_NO_ADMITE_DECISION_OBSERVACION_DETALLE(
            "artefactos.dominio.revisionasesor.error.no-admite-decision-observacion-detalle", 2),
    ERROR_VERSION_SIGUIENTE_NO_REGISTRADA(
            "artefactos.dominio.revisionasesor.error.version-siguiente-no-registrada", 0),
    ERROR_VERSION_SIGUIENTE_NO_REGISTRADA_DETALLE(
            "artefactos.dominio.revisionasesor.error.version-siguiente-no-registrada-detalle", 2),
    ERROR_VERSION_SIGUIENTE_ENVIADA("artefactos.dominio.revisionasesor.error.version-siguiente-enviada", 0),
    ERROR_VERSION_SIGUIENTE_ENVIADA_DETALLE(
            "artefactos.dominio.revisionasesor.error.version-siguiente-enviada-detalle", 2),
    LOG_GUARDADA("artefactos.infraestructura.revisionasesor.log.guardada", 1);

    private final String clave;
    private final int parametros;

    RevisionAsesorKey(String clave, int parametros) {
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
