package com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionAsesorOutputPort {

    void registrar(EvaluacionAsesorEntity evaluacionAsesor);

    Optional<EvaluacionAsesorEntity> obtenerPorEvaluacion(UUID evaluacion);

    boolean existenRegistrosPorEvaluacion(UUID evaluacion);

    void actualizarAsesor(UUID evaluacionAsesor, UUID asesor);
}
