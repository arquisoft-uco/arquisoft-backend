package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudianteArtefactoKey implements ClaveMensaje {

    LOG_CONSULTANDO("artefactos.aplicacion.estudianteartefacto.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("artefactos.aplicacion.estudianteartefacto.log.consulta-completada", 1),
    LOG_CONSULTANDO_PARTICIPACION("artefactos.aplicacion.estudianteartefacto.log.consultando-participacion", 1),
    LOG_CONSULTA_PARTICIPACION_COMPLETADA(
            "artefactos.aplicacion.estudianteartefacto.log.consulta-participacion-completada", 1),
    LOG_ASIGNANDO("artefactos.aplicacion.estudianteartefacto.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("artefactos.aplicacion.estudianteartefacto.log.verificacion-asignar", 6),
    LOG_ASIGNADOS("artefactos.aplicacion.estudianteartefacto.log.asignados", 2),
    LOG_GUARDADOS("artefactos.infraestructura.estudianteartefacto.log.guardados", 1),
    LOG_DESVINCULANDO("artefactos.aplicacion.estudianteartefacto.log.desvinculando", 2),
    LOG_DESVINCULADOS("artefactos.infraestructura.estudianteartefacto.log.desvinculados", 2),
    LOG_DEPURANDO("artefactos.aplicacion.estudianteartefacto.log.depurando", 1),
    LOG_VINCULOS_DEPURADOS("artefactos.infraestructura.estudianteartefacto.log.vinculos-depurados", 1),
    ERROR_SUPERAN_PROYECTO("artefactos.dominio.estudianteartefacto.error.superan-proyecto", 1),
    ERROR_SUPERAN_PROYECTO_DETALLE("artefactos.dominio.estudianteartefacto.error.superan-proyecto-detalle", 2),
    ERROR_YA_VINCULADO("artefactos.dominio.estudianteartefacto.error.ya-vinculado", 1),
    ERROR_YA_VINCULADO_DETALLE("artefactos.dominio.estudianteartefacto.error.ya-vinculado-detalle", 1),
    ERROR_NO_VINCULADO_PROYECTO_GRADO(
            "artefactos.dominio.estudianteartefacto.error.no-vinculado-proyecto-grado", 1),
    ERROR_NO_VINCULADO_PROYECTO_GRADO_DETALLE(
            "artefactos.dominio.estudianteartefacto.error.no-vinculado-proyecto-grado-detalle", 1);

    private final String clave;
    private final int parametros;

    EstudianteArtefactoKey(String clave, int parametros) {
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
