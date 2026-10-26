package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de la asignación de artefactos a un proyecto de grado. */
public enum ArtefactoKey implements ClaveMensaje {

    LOG_ASIGNANDO("artefactos.aplicacion.artefacto.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("artefactos.aplicacion.artefacto.log.verificacion-asignar", 5),
    LOG_ASIGNADOS("artefactos.aplicacion.artefacto.log.asignados", 2),
    LOG_TRASPASANDO("artefactos.aplicacion.artefacto.log.traspasando", 2),
    LOG_CONSULTANDO_ASESOR("artefactos.aplicacion.artefacto.log.consultando-asesor", 4),
    LOG_CONSULTA_ASESOR_COMPLETADA("artefactos.aplicacion.artefacto.log.consulta-asesor-completada", 1),
    LOG_GUARDADOS("artefactos.infraestructura.artefacto.log.guardados", 1),
    LOG_TRASPASADOS("artefactos.infraestructura.artefacto.log.traspasados", 1),
    ERROR_REPOSITORIO_YA_ASIGNADO("artefactos.dominio.artefacto.error.repositorio-ya-asignado", 1),
    ERROR_NO_ENCONTRADO("artefactos.dominio.artefacto.error.no-encontrado", 1),
    ERROR_ESTUDIANTE_NO_PROPIETARIO("artefactos.dominio.artefacto.error.estudiante-no-propietario", 2);

    private final String clave;
    private final int parametros;

    ArtefactoKey(String clave, int parametros) {
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
