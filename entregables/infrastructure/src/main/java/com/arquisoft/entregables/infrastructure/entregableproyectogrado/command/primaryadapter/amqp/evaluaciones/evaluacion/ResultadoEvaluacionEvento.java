package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.shared.util.UtilEnum;
import com.arquisoft.shared.util.UtilTexto;

public enum ResultadoEvaluacionEvento {

    APROBADA("APROBADO"),
    APROBADA_CON_OBSERVACIONES("APROBADO_CON_OBSERVACIONES"),
    RECHAZADA("RECHAZADO");

    private final String estadoEntregable;

    ResultadoEvaluacionEvento(String estadoEntregable) {
        this.estadoEntregable = estadoEntregable;
    }

    public String getEstadoEntregable() {
        return estadoEntregable;
    }

    public static String estadoEntregableDe(String resultado) {
        return UtilEnum.desde(ResultadoEvaluacionEvento.class, resultado)
                .map(ResultadoEvaluacionEvento::getEstadoEntregable)
                .orElse(UtilTexto.VACIO);
    }
}
