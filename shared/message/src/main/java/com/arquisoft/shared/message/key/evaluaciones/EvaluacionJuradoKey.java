package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionJuradoKey implements ClaveMensaje {

    ERROR_JURADOS_NO_ENCONTRADOS("evaluaciones.dominio.evaluacionjurado.error.jurados-no-encontrados", 0),
    ERROR_JURADOS_NO_ENCONTRADOS_DETALLE("evaluaciones.dominio.evaluacionjurado.error.jurados-no-encontrados-detalle", 1),
    ERROR_JURADOS_YA_ASIGNADOS("evaluaciones.dominio.evaluacionjurado.error.jurados-ya-asignados", 0),
    ERROR_JURADOS_YA_ASIGNADOS_DETALLE("evaluaciones.dominio.evaluacionjurado.error.jurados-ya-asignados-detalle", 1),
    LOG_ASIGNANDO("evaluaciones.aplicacion.evaluacionjurado.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("evaluaciones.aplicacion.evaluacionjurado.log.verificacion-asignar", 4),
    LOG_ASIGNADOS("evaluaciones.aplicacion.evaluacionjurado.log.asignados", 2),
    LOG_LOTE_GUARDADO("evaluaciones.infraestructura.evaluacionjurado.log.lote-guardado", 1),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacionjurado.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacionjurado.log.consulta-completada", 3),
    LOG_CONSULTANDO_ASIGNADAS("evaluaciones.aplicacion.evaluacionjurado.log.consultando-asignadas", 1),
    LOG_CONSULTA_ASIGNADAS_COMPLETADA(
            "evaluaciones.aplicacion.evaluacionjurado.log.consulta-asignadas-completada", 1);

    private final String clave;
    private final int parametros;

    EvaluacionJuradoKey(String clave, int parametros) {
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
