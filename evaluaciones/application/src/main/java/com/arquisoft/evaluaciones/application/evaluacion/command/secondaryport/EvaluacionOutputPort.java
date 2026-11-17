package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ContextoAsignacionEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionOutputPort {

    void registrar(EvaluacionEntity evaluacion);

    boolean existePorEntregable(UUID entregable);

    void actualizarEstado(UUID evaluacion, String estado);

    Optional<ContextoAsignacionEvaluacionEntity> obtenerContextoAsignacionBloqueado(UUID evaluacion);
}
