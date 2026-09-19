package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;

import java.util.UUID;

public interface EvaluacionJuradoOutputPort {

    EstadoEvaluacionJuradoEntity obtenerEstado(UUID evaluacionJurado, UUID jurado);

    boolean estaFinalizada(UUID evaluacionJurado);
}
