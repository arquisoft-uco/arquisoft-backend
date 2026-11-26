package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionKey implements ClaveMensaje {

    ERROR_RESULTADO_NO_ENCONTRADO("evaluaciones.dominio.evaluacion.error.resultado-no-encontrado", 0),
    ERROR_RESULTADO_NO_ENCONTRADO_DETALLE("evaluaciones.dominio.evaluacion.error.resultado-no-encontrado-detalle", 1),
    ERROR_DESTINATARIOS_COORDINADOR("evaluaciones.dominio.evaluacion.error.destinatarios-coordinador", 0),
    ERROR_DESTINATARIOS_ASESOR("evaluaciones.dominio.evaluacion.error.destinatarios-asesor", 0),
    ERROR_DESTINATARIOS_ESTUDIANTES("evaluaciones.dominio.evaluacion.error.destinatarios-estudiantes", 0),
    ERROR_DESTINATARIOS_INCOMPLETOS_DETALLE("evaluaciones.dominio.evaluacion.error.destinatarios-incompletos-detalle", 1),
    ERROR_RESUMENES_ASESOR("evaluaciones.dominio.evaluacion.error.resumenes-asesor", 0),
    ERROR_RESUMENES_JURADOS("evaluaciones.dominio.evaluacion.error.resumenes-jurados", 0),
    ERROR_RESUMENES_INCOMPLETOS_DETALLE("evaluaciones.dominio.evaluacion.error.resumenes-incompletos-detalle", 1),
    LOG_VERIFICACION_FINALIZACION("evaluaciones.aplicacion.evaluacion.log.verificacion-finalizacion", 4),
    LOG_FINALIZADA("evaluaciones.aplicacion.evaluacion.log.finalizada", 3),
    LOG_FINALIZACION_GUARDADA("evaluaciones.infraestructura.evaluacion.log.finalizacion-guardada", 2),
    LOG_NOTAS_ACTUALIZADAS("evaluaciones.infraestructura.evaluacion.log.notas-actualizadas", 1),
    LOG_NOTAS_CONSOLIDADAS("evaluaciones.aplicacion.evaluacion.log.notas-consolidadas", 4),
    LOG_CRITERIO_CUMPLIMIENTO_AUSENTE("evaluaciones.infraestructura.evaluacion.log.criterio-cumplimiento-ausente", 2),
    ERROR_FINALIZADA("evaluaciones.dominio.evaluacion.error.finalizada", 0),
    ERROR_ASIGNACION_FINALIZADA("evaluaciones.dominio.evaluacion.error.asignacion-finalizada", 0),
    ERROR_ESTADO_NO_ENCONTRADO("evaluaciones.dominio.evaluacion.error.estado-no-encontrado", 0),
    ERROR_ESTADO_NO_ENCONTRADO_DETALLE("evaluaciones.dominio.evaluacion.error.estado-no-encontrado-detalle", 1),
    ERROR_NO_ENCONTRADA("evaluaciones.dominio.evaluacion.error.no-encontrada", 0),
    ERROR_NO_ENCONTRADA_DETALLE("evaluaciones.dominio.evaluacion.error.no-encontrada-detalle", 1),
    LOG_INICIADA("evaluaciones.aplicacion.evaluacion.log.iniciada", 3),
    LOG_ESTADO_ACTUALIZADO("evaluaciones.infraestructura.evaluacion.log.estado-actualizado", 2),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacion.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacion.log.consulta-completada", 3),
    LOG_VERIFICACION_GENERAR("evaluaciones.aplicacion.evaluacion.log.verificacion-generar", 4),
    LOG_GENERADA("evaluaciones.infraestructura.evaluacion.log.generada", 2),
    LOG_DUPLICADA("evaluaciones.infraestructura.evaluacion.log.duplicada", 1),
    LOG_GUARDADA("evaluaciones.infraestructura.evaluacion.log.guardada", 1);

    private final String clave;
    private final int parametros;

    EvaluacionKey(String clave, int parametros) {
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
