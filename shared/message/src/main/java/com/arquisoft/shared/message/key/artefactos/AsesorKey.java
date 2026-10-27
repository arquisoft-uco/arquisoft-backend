package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Asesor (réplica local en artefactos). */
public enum AsesorKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("artefactos.infraestructura.asesor.log.agregado-recibido", 3),
    LOG_AGREGADO("artefactos.aplicacion.asesor.log.agregado", 1),
    LOG_DUPLICADO("artefactos.aplicacion.asesor.log.duplicado", 1),
    LOG_DESCARTADO("artefactos.aplicacion.asesor.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("artefactos.aplicacion.asesor.log.verificacion-agregar", 2),
    LOG_GUARDADO("artefactos.infraestructura.asesor.log.guardado", 1),
    LOG_ACTUALIZADO("artefactos.infraestructura.asesor.log.actualizado", 1),
    LOG_VERIFICACION_ACTUALIZAR("artefactos.aplicacion.asesor.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("artefactos.infraestructura.asesor.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("artefactos.infraestructura.asesor.log.actualizacion-no-replicado", 1),
    LOG_REMOVIDO_RECIBIDO("artefactos.infraestructura.asesor.log.removido-recibido", 2),
    LOG_VERIFICACION_REMOVER("artefactos.aplicacion.asesor.log.verificacion-remover", 2),
    LOG_REMOVIDO("artefactos.aplicacion.asesor.log.removido", 1),
    LOG_LAPIDA("artefactos.aplicacion.asesor.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("artefactos.aplicacion.asesor.log.remocion-descartada", 2),
    LOG_USUARIO_MODIFICADO_RECIBIDO("artefactos.infraestructura.asesor.log.usuario-modificado-recibido", 2),
    LOG_REACTIVADO("artefactos.aplicacion.asesor.log.reactivado", 1),
    ERROR_NO_VIGENTE("artefactos.dominio.asesor.error.no-vigente", 0),
    ERROR_NO_VIGENTE_DETALLE("artefactos.dominio.asesor.error.no-vigente-detalle", 1),
    ERROR_DE_REVISION_NO_ENCONTRADO("artefactos.dominio.asesor.error.de-revision-no-encontrado", 1);

    private final String clave;
    private final int parametros;

    AsesorKey(String clave, int parametros) {
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
