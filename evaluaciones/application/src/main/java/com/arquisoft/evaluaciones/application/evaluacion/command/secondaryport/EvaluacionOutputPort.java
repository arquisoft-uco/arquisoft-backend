package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.AvanceEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ConsolidacionNotasEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ContextoAsignacionEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.FinalizacionEvaluacionEntity;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionOutputPort {

    void registrar(EvaluacionEntity evaluacion);

    boolean existePorEntregable(UUID entregable);

    void actualizarEstado(UUID evaluacion, String estado);

    void actualizarNotas(ConsolidacionNotasEvaluacionEntity consolidacion);

    Optional<AvanceEvaluacionEntity> obtenerAvance(UUID evaluacion);

    boolean existenObservaciones(UUID evaluacion);

    void finalizar(FinalizacionEvaluacionEntity finalizacion);

    Optional<ContextoAsignacionEvaluacionEntity> obtenerContextoAsignacionBloqueado(UUID evaluacion);
}
