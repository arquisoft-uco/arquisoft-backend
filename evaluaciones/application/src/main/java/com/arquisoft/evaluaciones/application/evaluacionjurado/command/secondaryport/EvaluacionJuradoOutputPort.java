package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionJuradoOutputPort {

    Optional<EstadoEvaluacionJuradoEntity> obtenerEstadoBloqueado(UUID evaluacionJurado);
}
