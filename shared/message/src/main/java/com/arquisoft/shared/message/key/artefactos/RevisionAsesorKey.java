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
            "artefactos.dominio.revisionasesor.error.estudiante-no-participa-detalle", 2);

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
