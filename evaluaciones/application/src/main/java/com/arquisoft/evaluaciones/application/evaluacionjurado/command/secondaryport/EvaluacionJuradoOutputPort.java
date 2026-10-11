package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionJuradoOutputPort {

    Optional<EstadoEvaluacionJuradoEntity> obtenerEstadoBloqueado(UUID evaluacionJurado);

    void registrarTodas(List<EvaluacionJuradoEntity> entidades);

    Set<UUID> consultarJuradosAsignados(UUID evaluacion, Set<UUID> jurados);

    boolean estaFinalizada(UUID evaluacionCuantitativaJurado);
}
