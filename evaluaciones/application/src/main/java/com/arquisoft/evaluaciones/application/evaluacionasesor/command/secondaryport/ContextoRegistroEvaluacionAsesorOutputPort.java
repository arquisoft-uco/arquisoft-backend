package com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.ContextoRegistroEvaluacionAsesorEntity;

import java.util.Optional;
import java.util.UUID;

public interface ContextoRegistroEvaluacionAsesorOutputPort {

    Optional<ContextoRegistroEvaluacionAsesorEntity> obtenerContextoBloqueado(UUID evaluacionAsesor);
}
