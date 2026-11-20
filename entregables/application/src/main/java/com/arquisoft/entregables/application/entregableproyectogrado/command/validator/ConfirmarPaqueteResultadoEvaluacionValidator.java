package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;

public interface ConfirmarPaqueteResultadoEvaluacionValidator {

    void validar(ResultadoEvaluacionEntregableProyectoGradoDomain resultado, PaqueteEntregableDomain paquete);
}
