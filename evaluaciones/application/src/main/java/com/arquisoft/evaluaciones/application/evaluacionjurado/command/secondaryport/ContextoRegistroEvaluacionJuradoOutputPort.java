package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ContextoRegistroEvaluacionJuradoOutputPort {

    Optional<ContextoRegistroEvaluacionJuradoEntity> obtenerContextoBloqueado(UUID evaluacionJurado);
}
